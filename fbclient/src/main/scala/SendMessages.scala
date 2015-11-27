import akka.actor.ActorSystem
import akka.io.IO
import spray.client.pipelining._
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Future, Await}
import spray.can.Http
import spray.http._
import scala.util.{Failure, Success}

case class AddFriend(myID : Identifier, friendID : Identifier)

class SendMessages() {

  def send(uri : String) = {
    implicit val timeout = Timeout(5.seconds)
    implicit val system = ActorSystem()
    import system.dispatcher
    // execution context for futures

    val pipeline: Future[SendReceive] =
      for (
        Http.HostConnectorInfo(connector, _) <-
        IO(Http) ? Http.HostConnectorSetup("localhost", port = 8080)
      ) yield sendReceive(connector)

    val request = Get(uri)
    val response: Future[HttpResponse] = pipeline.flatMap(_(request))
    response onComplete {
      case Success(r) => println(r.entity.asString)
      case Failure(e) => e
    }
  }

}
