import akka.actor._
import scala.concurrent.{Await, Future}
import spray.http._
import spray.client.pipelining._
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.client.pipelining._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Success, Failure}

class Network {
  implicit val timeout = Timeout(5.seconds)
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
  }
}
