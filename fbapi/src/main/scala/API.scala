import java.util.TimeZone
import akka.util.Timeout
import spray.http.StatusCodes.Unauthorized
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
// DO NOT remove this execution ctx as it is needed by Spray session deep down
import ExecutionContext.Implicits.global
import com.github.nscala_time.time.Imports.DateTime
import akka.actor.{ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import spray.routing._
import session._
import session.directives._

import FacebookJsonSupport._

import scala.collection.mutable

// simple actor that handles the routes.
class API extends Actor with HttpService with StatefulSessionManagerDirectives[Int] {

  // required as implicit value for the HttpService
  // included from SJService
  implicit val actorRefFactory = context.system
  //implicit val system : ActorSystem = actorRefFactory.system

  var map = mutable.HashMap[Identifier, UserEnt]()

  // we don't create a receive function ourselves, but use
  // the runRoute function from the HttpService to create
  // one for us, based on the supplied routes.
  def receive = runRoute(sessionRoute)

  // Path directive that extracts a Facebook ID
  def ObjectID = path(FBID)

  val invalidSessionHandler = RejectionHandler {
    case InvalidSessionRejection(id) :: _ =>
      complete((Unauthorized, s"Unknown session $id"))
  }

  // needed as an implicit for Session timeout. The library does not currently use this
  implicit val timeout = Timeout(5.seconds)
  implicit val manager = new InMemorySessionManager[Int](ConfigFactory.load())

  lazy val sessionRoute = {
    handleRejections(invalidSessionHandler) {
      cookieSession() { (session_id, session_map) =>
        routes(session_id, session_map)
      }
    }
  }

  // handles the other path, we could also define these in separate files
  def routes(session_id : String, session_map : Map[String, Int]) =
    pathPrefix("user") {
      path("test") {
        get {
          complete {
            new UserEnt()
          }
        }
      } ~
      path("friends") {
        get {
          complete {
            "Getting user friends"
          }
        }
      } ~
      ObjectID { id =>
        get {
          complete {
            if (map.contains(new Identifier(id))) {
              map {
                new Identifier(id)
              }.asInstanceOf[FacebookEntity]
            } else {
              "Unknown ID"
            }
          }
        }
      } ~
      pathEndOrSingleSlash {
        post {
          // Receive a JSON entity that acts as a form
          entity(as[UserCreateForm]) { user =>
            complete {
              val id = new Identifier(0)
              // TODO: validate the user's form fields
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

              map += (id -> ent)
              ent
            }
          }
        } ~
        get {
          complete("Yourself")
        }
      }
    } ~
    pathPrefix("comment") {
      ObjectID { id =>
        get {
          complete {
            s"Comment $id"
          }
        } ~
          put {
            complete {
              "Editing comment"
            }
          } ~
          delete {
            complete {
              "Removing comment"
            }
          }
      } ~
      post {
        complete {
          "Creating comment"
        }
      }
    } ~
    pathPrefix("profile") {
      ObjectID { id =>
        get {
          complete {
            // TODO: perform redirect to the appropriate object: Page or User
            s"Profile $id"
          }
        }
      } ~
      get {
        complete {
          // TODO: redirect to user's profile (/user)
          // In our Facebook model, you must be logged in as a user account, so this always
          // returns a user
          "Get the logged in user's profile"
        }
      }
    } ~
    pathPrefix("page") {
      ObjectID { id =>
        get {
          complete {
            s"Page $id"
          }
        }
      } ~
      post {
        complete {
          "Creating page"
        }
      }
    } ~
    pathPrefix("like") {
      ObjectID { id =>
        get {
          complete {
            s"Getting likes for $id"
          }
        } ~
        post {
          complete {
            s"Liking object $id"
          }
        } ~
        delete {
          complete {
            s"Removing like from object $id"
          }
        }
      }
    } ~
    pathPrefix("message") {
      path(MessageThreadID) { id =>
        get {
          complete {
            s"Getting messages for thread $id"
          }
        } ~
          post {
            complete {
              s"New message for thread $id"
            }
          }
      } ~
      path("threads") {
        get {
          complete {
            "Getting list of threads"
          }
        }
      } ~
      // Who to send the message to
      ObjectID { id =>
        get {
          complete {
            s"Getting messages for thread $id"
          }
        }
        post {
          complete {
            // This may reuse an existing message thread or start a new one
            "New message to person"
          }
        }
      }
    }
}
