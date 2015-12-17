import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import spray.routing.RequestContext

import akka.pattern.ask
import scala.collection.mutable
import spray.http.StatusCodes._
import FacebookJsonSupport._
import spray.json._

case class UserCreated(ctx : Context, ent : UserEnt)
case class UserEntAssociated(id : Identifier)

class UserEntActor(friendService : ActorRef,
                   identService : ActorRef,
                   keychain : ActorRef) extends Actor with ActorLogging {
  var userMap = mutable.HashMap[Identifier, UserEnt]()

  def receive = {
    case Get(ctx, id) =>
      // no actor, because no key is necessary
      if (userExists(id)) {
        ctx.req.complete(userById(id))
      } else {
        ctx.req.complete("Unknown User ID")
      }
    case x : Create =>
      context.actorOf(Props(new CreateNewEntityActor(keychain, identService, x, None)))

    // called when a new entity ID was captured. create is an object so it must be casted
    case EntCreated(ctx, id, create) =>
      val form = create.data.asInstanceOf[UserCreateForm]

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

      userMap += (id -> ent)

      // let the friend service know that we have created a user
      friendService ! UserCreated(ctx, ent)
    case _ => log.error("Unknown message")
  }

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
}
