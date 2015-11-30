import akka.actor.{ActorLogging, Actor}
import spray.routing.RequestContext
import spray.http.StatusCodes._
import spray.http.{StatusCode, StatusCodes}
import scala.collection.mutable
import FacebookJsonSupport._

// Entity creation
case class CreateUser(ctx : RequestContext, form : UserCreateForm)
case class CreatePage(ctx : RequestContext, form : PageCreateForm)
case class CreatePost(ctx : RequestContext, owner : Identifier, target : Identifier, form : PostCreateForm)
case class CreateAlbum(ctx : RequestContext, owner : Identifier, form : AlbumCreateForm)
case class CreatePicture(ctx : RequestContext, albumId : Identifier, form : PictureCreateForm)

// Entity retrieval
case class GetUser(ctx : RequestContext, id : Identifier)
case class GetPage(ctx : RequestContext, id : Identifier)
case class GetPost(ctx : RequestContext, id : Identifier)
case class GetAlbum(ctx : RequestContext, id : Identifier)
case class GetPicture(ctx : RequestContext, id : Identifier)
case class GetEntOfType(ctx : RequestContext, id : Identifier, objType : FacebookEntityType.EntityType)

// Actions
case class AddFriend(ctx : RequestContext, requester : Identifier, target : Identifier)

// Queries
case class GetFriendsList(ctx : RequestContext, uid : Identifier)
case class GetPostsByUser(ctx : RequestContext, uid : Identifier)
case class GetUserList(ctx : RequestContext, uid : Identifier)
case class GetPageList(ctx : RequestContext, uid : Identifier)

// TODO WANT: Editing functions
// TODO WANT: Removal functions


class DataObjectActor extends Actor with ActorLogging {
  // Response wrapper
  type FacebookResponse = (StatusCode, Object)

  // Global ID counter
  var nextId = 0

  // [ Entity storage ]
  var userMap = mutable.HashMap[Identifier, UserEnt]()
  var pageMap = mutable.HashMap[Identifier, PageEnt]()
  var postMap = mutable.HashMap[Identifier, PostEnt]()
  var albumMap = mutable.HashMap[Identifier, AlbumEnt]()
  var pictureMap = mutable.HashMap[Identifier, PictureEnt]()

  // [ Metadata storage ]
  // Stores the corresponding entity types for an identifier
  var typeMap = mutable.HashMap[Identifier, FacebookEntity]()

  // [ Auxiliary object storage ]
  var friendsList = mutable.HashMap[Identifier, FriendsList]()

  /////////////////////////////////////////////////////////////////////////

  /**
    * Helper method that converts a FacebookResponse to a marshallable objet
    * @param ctx Request context
    * @param resp Facebook response content
    */
  def finalize(ctx : RequestContext, resp : FacebookResponse) = {
    val code = resp._1

    resp._2 match {
      case e : UserEnt =>
        ctx.complete(code, e)
      case e : PageEnt =>
        ctx.complete(code, e)
      case e : PostEnt =>
        ctx.complete(code, e)
      case e : AlbumEnt =>
        ctx.complete(code, e)
      case e : PictureEnt =>
        ctx.complete(code, e)
      case e : String =>
        ctx.complete(code, e)
      case _ => throw new Exception("Unsupported object return type")
    }
  }

  def receive = {
    // ################# Creation
    case CreateUser(ctx, form) =>
      log.info("Creating user " + form.first_name)
      finalize(ctx, createUser(form))
    case CreatePage(ctx, form) =>
      log.info("Creating page " + form.toString)
      finalize(ctx, createPage(form))
    case CreatePost(ctx, owner, target, form) =>
      log.info("Creating post " + form.content)
      finalize(ctx, createPost(owner,target,form))
    case CreateAlbum(ctx, owner, form) =>
      log.info("Creating album " + form.name)
      finalize(ctx, createAlbum(owner, form))
    case CreatePicture(ctx, albumId, form) =>
      log.info("Creating picture " + form.fileId)
      finalize(ctx, createPicture(albumId, form))

    // ################# Retrieval
    case GetUser(ctx, id) =>
      if (userExists(id)) {
        ctx.complete(userById(id))
      } else {
        ctx.complete("Unknown User ID")
      }
    case GetPage(ctx, id) =>
      if (pageMap.contains(id)) {
        ctx.complete(pageMap{id})
      } else {
        ctx.complete("Unknown Page ID")
      }
    case GetPost(ctx, id) =>
      if (postMap.contains(id)) {
        ctx.complete(postMap{id})
      } else {
        ctx.complete("Unknown Post ID")
      }
    case GetAlbum(ctx, id) =>
      if (albumMap.contains(id)) {
        ctx.complete(albumMap{id})
      } else {
        ctx.complete("Unknown Album ID")
      }
    case GetPicture(ctx, id) =>
      if (pictureMap.contains(id)) {
        ctx.complete(pictureMap{id})
      } else {
        ctx.complete("Unknown Picture ID")
      }

    // ################# Actions
    case AddFriend(ctx, requester, target) =>
      log.info(s"Adding friend $requester <-> $target")
      finalize(ctx, addFriend(requester, target))

    // ################# Queries
    case GetFriendsList(ctx, uid) =>
      if(!userExists(uid)) {
        ctx.complete((NotFound, "Could not find the user"))
      }

      log.info("TODO: GetFriendsList")

      ctx.complete("Not complete")
    case _ => log.debug("Unknown message")
  }


  /**
    * Creates a new user with the specified form parameters and returns the resulting UserEnt
    * @param form the user creation form
    * @return FacebookResponse
    */
  def createUser(form : UserCreateForm) : FacebookResponse = {
    val id = new Identifier(getNextId)

    val ent = new UserEnt(id,
      first_name = form.first_name,
      last_name = form.last_name,
      birthday = form.birthday,
      gender = form.gender,
      email = form.email,
      about = form.about,
      relationship_status = form.relationship_status,
      interested_in = form.interested_in,
      political = form.political,
      tz = form.tz,
      status = "" // TODO: either remove this field or get some data
    )

    // start with an empty friends list
    friendsList += (id -> FriendsList(mutable.MutableList()))
    userMap += (id -> ent)

    (OK, ent)
  }

  /**
    * Creates a new Facebook page
    * @param form the input form for the page
    * @return FacebookResponse
    */
  def createPage(form : PageCreateForm) : FacebookResponse = {
    val id = new Identifier(getNextId)

    val ent = new PageEnt(id,
      name = form.name,
      about = form.about,
      business = form.business,
      contact_address = form.contact_address,
      description = form.description,
      location = form.location,
      phone_number = form.phone_number
    )

    pageMap += (id -> ent)

    (OK, ent)
  }

  /**
    * Creates a post as `requester` to be placed on `target`'s wall
    * `target` can be any object that as a wall (User or Page)
    * @param form form with the post creation parameters
    * @return FacebookResponse
    */
  def createPost(ownerId : Identifier, targetId : Identifier, form : PostCreateForm) : FacebookResponse = {
    val id = new Identifier(getNextId)

    val ent = new PostEnt(id,
    owner = ownerId,
    target = targetId,
    content = form.content
    )

    postMap += (id -> ent)

    (OK, ent)
  }

  /**
    * Creates a new album for pictures to be stored under
    * @param form form fields for creating a album
    * @return FacebookResponse
    */
  def createAlbum(ownerId : Identifier, form : AlbumCreateForm) : FacebookResponse = {
    val id = new Identifier(getNextId)

    val ent = new AlbumEnt(id,
    owner = ownerId,
    name = form.name,
    description = form.description
    )

    albumMap += (id -> ent)

    (OK, ent)
  }

  /**
    * Creates a new picture in an album
    * @param form form fields for creating a picture
    * @return FacebookResponse
    */
  def createPicture(pAlbumId : Identifier, form : PictureCreateForm) : FacebookResponse = {
    val id = new Identifier(getNextId)

    val ent = new PictureEnt(id,
    albumId = pAlbumId,
    caption = form.caption,
    fileId = form.fileId
    )

    pictureMap += (id -> ent)

    (OK, ent)
  }

  ////////////////////////////////////////////////////////
  // Action functions
  ////////////////////////////////////////////////////////

  /**
    * Makes two specified UIDs friends
    * @param requester the person requesting the friend
    * @param target the target of the person requesting the friend
    * @return FacebookResponse
    */
  def addFriend(requester : Identifier, target : Identifier) : FacebookResponse = {
    if(!userExists(requester)) {
      return (NotFound, "Invalid user ID")
    }

    if(!userExists(target)) {
      return (NotFound, "Invalid target ID")
    }

    val reqList = friendsList{requester}.friends
    val tgtList = friendsList{target}.friends

    if(reqList.contains(target) || tgtList.contains(requester)) {
      return (NotAcceptable, "You are already friends")
    }

    reqList += target
    tgtList += requester

    (OK, "You are now friends with " + target)
  }

  ////////////////////////////////////////////////////////
  // Helper functions
  ////////////////////////////////////////////////////////

  /**
    * Gets a UserEnt by ID. The user ID must be valid
    * @param id user ID
    * @return UserEnt
    */
  def userById(id : Identifier) : UserEnt = {
    if(!userExists(id))
      throw new Exception("Invalid user id")

    userMap.get(id).get
  }

  /**
    * Checks if a user ID is valid
    * @param id user ID
    * @return Boolean
    */
  def userExists(id : Identifier) : Boolean = {
    userMap.contains(id)
  }

  /**
    * Creates and returns a new unique identifier
    * @return Identifier
    */
  def getNextId = {
    val id = nextId
    nextId += 1
    id
  }
}
