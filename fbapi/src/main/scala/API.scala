import akka.actor.Actor
import spray.routing._
import spray.http._

import PersonJsonSupport._

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
        entity(as[UserEnt]) { user =>
          complete { s"Hello ${user.first_name} ${user.last_name}" }
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
