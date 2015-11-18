import java.util.TimeZone
import com.github.nscala_time.time.Imports._
import akka.actor.Actor
import spray.routing._
//import spray.http._

import FacebookJsonSupport._

// simple actor that handles the routes.
class API extends Actor with HttpService {

  // required as implicit value for the HttpService
  // included from SJService
  def actorRefFactory = context

  // we don't create a receive function ourselve, but use
  // the runRoute function from the HttpService to create
  // one for us, based on the supplied routes.
  def receive = runRoute(testRoute)

  // Path directive that extracts a Facebook ID
  def ObjectID = path(FBID)
  def TextResp(s : String) = complete { s }

  // handles the other path, we could also define these in separate files
  // This is just a simple route to explain the concept
  val testRoute = {
    pathPrefix("user") {
      path("test") {
        get {
          complete {
            new UserEnt
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
      post {
        // Receive a JSON entity that acts as a form
        entity(as[UserCreateForm]) { user =>
          complete {
            // TODO: validate the user's form fields
            new UserEnt(new Identifier(0),
              first_name = user.first_name,
              last_name = user.last_name,
              birthday =  user.birthday,
              gender = user.gender,
              email = user.email,
              about = user.about,
              relationship_status = user.relationship_status,
              interested_in = user.interested_in,
              political = user.political,
              tz = user.tz,
              last_updated = DateTime.now,
              status = ""
            )
          }
        }
      } ~
      get {
        TextResp("Yourself")
      }
    } ~
    pathPrefix("comment") {
      ObjectID { id =>
        get {
          complete { s"Comment $id"}
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
        }
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
}
