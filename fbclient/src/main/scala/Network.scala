import java.util.TimeZone
import akka.actor._
import spray.httpx.SprayJsonSupport
import scala.concurrent.{Await, Future}
import spray.http._
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.client.pipelining._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

class Network {
  /*implicit val timeout = Timeout(5.seconds)
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  val pipeline: Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) <-
      IO(Http) ? Http.HostConnectorSetup("localhost", port = 8080)
    ) yield sendReceive(connector)

  val request = Get("/user/test")
  val response: Future[HttpResponse] = pipeline.flatMap(_(request))
  response onComplete{
    case Success(r) => println(r.entity.asString)
    case Failure(e) => e
  }*/

  import com.github.nscala_time.time.Imports._
  def addUser(first_name : String,
              last_name : String,
              birthday : DateTime,
              gender : Gender.EnumVal,
              email : String,
              about : String,
              relationship_status : RelationshipStatus.EnumVal,
              interested_in : Gender.EnumVal,
              political : PoliticalAffiliation.EnumVal,
              tz : TimeZone) : Future[UserEnt] = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures
    import FacebookJsonSupport._

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )

    /*val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm("Chelsea", "Metcalf", DateTime.now, Gender.Female,
        "chelsea.metcalf@gmail.com", "Test about", RelationshipStatus.Single,
        Gender.Male, PoliticalAffiliation.Democrat, TimeZone.getDefault)))*/

    val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm(first_name, last_name, birthday, gender,
        email, about, relationship_status, interested_in, political, tz)))

    response
  }

  def getUser() : Future[UserEnt] = {
    implicit val system = ActorSystem()
    import system.dispatcher // execution context for futures
    import FacebookJsonSupport._

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )

    val response: Future[UserEnt] =
      pipeline(Get("http://localhost:8080/user/test"))
    
    response
  }

}
