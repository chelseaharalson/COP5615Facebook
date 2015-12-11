import java.io._

import akka.actor.{Cancellable, ActorLogging, Actor}
import spray.routing.RequestContext
import spray.http.StatusCodes._
import spray.http.{StatusCode, StatusCodes}
import scala.collection.mutable
import FacebookJsonSupport._
import scala.concurrent.duration._

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

// API Meta
case class PrintStats()
case class StopServer()

class DataObjectActor extends Actor with ActorLogging {
  // Response wrapper
  type FacebookResponse = (StatusCode, Object)

  // Count requests per second
  var counter : Long = 0
  var totalCounter : Long = 0
  var measurements = mutable.MutableList[Tuple2[Long, Double]]()

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

  // Stats collection
  var initialStartTime: Long = 0
  var startTime: Long = 0
  var endTime: Long = _
  var scheduler: Cancellable = _
  var frequency = new FiniteDuration(1000, MILLISECONDS)
  var stopIn = new FiniteDuration(60000, MILLISECONDS)
  var firstCount = true // when to start collecting stats

  def runStats() {
    import scala.concurrent.ExecutionContext.Implicits.global

    // start counting immediately
    startTime = System.currentTimeMillis
    initialStartTime = startTime

    // periodic stats collection
    scheduler = context.system.scheduler.schedule(frequency,
      frequency,
      self,
      PrintStats())

    // stop server signal
    scheduler = context.system.scheduler.scheduleOnce(
      stopIn,
      self,
      StopServer())
  }

  /////////////////////////////////////////////////////////////////////////

  /**
    * Helper method that converts a FacebookResponse to a marshallable object
    * This is a hack because I don't know of a better way to do this.
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
    // ################# Stats
    case PrintStats() =>
      println()
      println()
      val now = System.currentTimeMillis
      val reqPerSec = (counter.toDouble/(now - startTime).toDouble) * 1000
      log.info("**** The average number of requests per second is : %.2f".format(reqPerSec))

      measurements += new Pair(now-initialStartTime, reqPerSec)

      // reset the stats
      counter = 0
      startTime = System.currentTimeMillis()
    case StopServer() =>
      log.info("Stopping API server")

      printStats
      writeStatsLog

      context.system.shutdown()

    // ################# Creation
    case CreateUser(ctx, form) =>
      countReq
      log.info("Creating user " + form.first_name + " " + form.last_name + " " + form.public_key)
      finalize(ctx, createUser(form))
    case CreatePage(ctx, form) =>
      countReq
      log.info("Creating page " + form.description)
      finalize(ctx, createPage(form))
    case CreatePost(ctx, owner, target, form) =>
      countReq
      log.info("Creating post " + form.content + " FROM USER " + owner + " TO " + target)
      finalize(ctx, createPost(owner,target,form))
    case CreateAlbum(ctx, owner, form) =>
      countReq
      log.info("Creating album " + form.name)
      finalize(ctx, createAlbum(owner, form))
    case CreatePicture(ctx, albumId, form) =>
      countReq
      log.info("Creating picture " + form.caption)
      finalize(ctx, createPicture(albumId, form))

    // ################# Retrieval
    case GetUser(ctx, id) =>
      countReq
      if (userExists(id)) {
        ctx.complete(userById(id))
      } else {
        ctx.complete("Unknown User ID")
      }
    case GetPage(ctx, id) =>
      countReq
      if (pageMap.contains(id)) {
        ctx.complete(pageMap{id})
      } else {
        ctx.complete("Unknown Page ID")
      }
    case GetPost(ctx, id) =>
      countReq
      if (postMap.contains(id)) {
        ctx.complete(postMap{id})
      } else {
        ctx.complete("Unknown Post ID")
      }
    case GetAlbum(ctx, id) =>
      countReq
      if (albumMap.contains(id)) {
        ctx.complete(albumMap{id})
      } else {
        ctx.complete("Unknown Album ID")
      }
    case GetPicture(ctx, id) =>
      countReq
      if (pictureMap.contains(id)) {
        ctx.complete(pictureMap{id})
      } else {
        ctx.complete("Unknown Picture ID")
      }

    // ################# Actions
    case AddFriend(ctx, requester, target) =>
      countReq
      //log.info(s"Adding friend $requester <-> $target")
      finalize(ctx, addFriend(requester, target))

    // ################# Queries
    case GetFriendsList(ctx, uid) =>
      countReq
      if(!userExists(uid)) {
        ctx.complete((NotFound, "Could not find the user"))
      } else {
        finalize(ctx, getFriends(uid))
      }

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
      status = "", // TODO: either remove this field or get some data
      public_key = form.public_key
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

  /**
   * Gets friends for requester
   */
  def getFriends(requester : Identifier) : FacebookResponse = {
    val reqList = friendsList{requester}.friends
    (OK, "Here is your friend list: " + reqList)
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

  /**
    * Counts a request for Stats collection
    */
  def countReq = {
    if(firstCount) {
      firstCount = false
      runStats()
    }

    counter += 1
    totalCounter += 1
  }

  def getStatsInfo : (Double, Double, Long, String) = {
    var avg : Double = 0.0
    var max : Double = 0.0

    val builder = mutable.StringBuilder.newBuilder
    for(i <- measurements.indices) {
      val m : Double = measurements(i)._2
      builder.append("%.2f".format(m))

      if(i+1 != measurements.size)
        builder.append(", ")

      if(m > max)
        max = m

      avg += m
    }

    avg /= measurements.size

    (avg, max, totalCounter, builder.result())
  }

  def printStats = {
    val (avg, max, totalCounter, all) = getStatsInfo

    log.info("Final stats measurements: " + all)
    log.info("Peak R/S: %.2f".format(max))
    log.info("Average R/S: %.2f".format(avg))
    log.info("Total requests: " + totalCounter)
  }

  def writeStatsLog = {
    val logFile = new PrintWriter(new File("fbapi-stats-%d.csv".format(initialStartTime)))

    logFile.println("# Start time %d".format(initialStartTime))

    for(i <- measurements.indices) {
      logFile.println("%d,%.2f".format(measurements(i)._1, measurements(i)._2))

    }

    val (avg, max, totalCounter, _) = getStatsInfo
    logFile.println("# End time %d".format(System.currentTimeMillis))
    logFile.println("# Peak R/S: %.2f".format(max))
    logFile.println("# Average R/S: %.2f".format(avg))
    logFile.println("# Total requests: " + totalCounter)
    logFile.close()
  }
}
