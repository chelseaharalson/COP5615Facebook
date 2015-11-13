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
      path("create") {
        post {
          entity(as[UserEnt]) { user =>
            complete { s"Hello ${user.first_name} ${user.last_name}" }
          }
        }
      }
    }
  }
}
