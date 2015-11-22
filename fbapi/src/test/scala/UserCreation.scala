import org.specs2.mutable.Specification
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._
import FacebookJsonSupport._

class UserCreation extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system

  val userRoute = {
    pathPrefix("user") {
      path("test") {
        get {
          complete {
            new UserEnt()
          }
        }
      } ~
      post {
        entity(as[UserCreateForm]) { ent =>
          complete {
            new UserEnt()
          }
        }
      }
    }
  }

  case class BadPerson(id : String, first_name : String)
  implicit val badPersonJsonFormat = jsonFormat2(BadPerson)

  val invalidUser = new BadPerson("0", "Doge")

  "User creation" should {
    "Return a default user" in {
      Get("/user/test") ~> userRoute ~> check {
        responseAs[UserEnt]
        status === OK
      }
    }

    "Accept a user" in {
      Post("/user", new UserEnt()) ~> userRoute ~> check {
        responseAs[UserEnt]
        status === OK
      }
    }

    "Reject an invalid user" in {
      Post("/user", invalidUser) ~> sealRoute(userRoute) ~> check {
        status === BadRequest
      }
    }
  }

}
