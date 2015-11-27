import akka.actor.{ActorLogging, Actor}
import spray.routing.RequestContext
import spray.http.StatusCodes._
import spray.http.{StatusCode, StatusCodes}

import scala.collection.mutable
import FacebookJsonSupport._

case class CreateUser(ctx : RequestContext, user : UserCreateForm, session : Session)
case class RetrieveUser(ctx : RequestContext, id : Identifier)
case class AddFriend(ctx : RequestContext, requester : Identifier, target : Identifier)
case class AddFriendX(ctx : RequestContext, myID : Identifier, friendID : Identifier)
case class AddPost(ctx : RequestContext, myID : Identifier, friendID : Identifier, content : String)
case class AddAlbum(ctx : RequestContext, userID : Identifier, albumID : Identifier, albumName : String)

class DataObjectActor extends Actor with ActorLogging {
  var userMap = mutable.HashMap[Identifier, UserEnt]()
  var friendsLists = mutable.HashMap[Identifier, FriendsList]()
  var nextId = 0

  def receive = {
    case CreateUser(ctx, user, session) =>
      log.info("Creating user " + user.toString)

      val id = new Identifier(getNextId)

      val ent = new UserEnt(id,
        first_name = user.first_name,
        last_name = user.last_name,
        birthday = user.birthday,
        gender = user.gender,
        email = user.email,
        about = user.about,
        relationship_status = user.relationship_status,
        interested_in = user.interested_in,
        political = user.political,
        tz = user.tz,
        status = ""
      )

      // start with an empty friends list
      friendsLists += (id -> FriendsList(mutable.MutableList()))
      userMap += (id -> ent)
      // TODO: must use update session id
      session.setUserId(id)
      ctx.complete(ent)
    case AddFriend(ctx, requester, target) =>
      log.info(s"Adding friends $requester <-> $target")
      ctx.complete(addFriend(requester, target))
    case AddFriendX(ctx, myID, friendID) =>
      println("ADD FRIEND WORKS!!")
      ctx.complete(addFriend(myID, friendID))
    case AddPost(ctx, myID, friendID, content) =>
      println("ADDING POST...")
      ctx.complete(addPost(myID, friendID, content))
    case AddAlbum(ctx, userID, albumID, albumName) =>
      println("ADDING ALBUM...")
      ctx.complete(addAlbum(userID, albumID, albumName))
    case RetrieveUser(ctx, id) =>
      if (userMap.contains(id)) {
        ctx.complete(userMap{id}.asInstanceOf[FacebookEntity])
      } else {
        ctx.complete("Unknown ID")
      }
    case _ => log.debug("Unknown message")
  }

  def addPost(requester : Identifier, target : Identifier, content : String) : (StatusCode, String) = {
    // TODO : add post
    println("NEEDS TO BE FIXED : addPost")
    (OK, "Posting " + target)
  }

  def addAlbum(requester : Identifier, albumID : Identifier, content : String) : (StatusCode, String) = {
    // TODO : add album
    println("NEEDS TO BE FIXED : addAlbum")
    (OK, "Album " + albumID + " posted by " + requester)
  }

  def addFriend(requester : Identifier, target : Identifier) : (StatusCode, String) = {
    val from = userById(requester)
    val to = userById(target)

    /*if(!from.contains()) {
      return (NotFound, "Invalid user ID")
    }

    if(!to.contains()) {
      return (NotFound, "Invalid target ID")
    }*/

    //friendsLists{requester}.friends :+ target
    //friendsLists{target}.friends :+ requester

    println("NEEDS TO BE FIXED : addFriend in DataObjectActor.scala")

    (OK, "You are now friends with " + target)
  }
  def userById(id : Identifier) : Option[UserEnt] = {
    userMap.get(id)
  }

  def getNextId = {
    val id = nextId
    nextId += 1
    id
  }
}
