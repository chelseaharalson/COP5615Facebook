import java.io.File
import spray.http.{MediaTypes, BodyPart, MultipartFormData}
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

  def uploadFile() = {
    implicit val system = ActorSystem("simple-spray-client")
    import system.dispatcher // execution context for futures below

    val pipeline = sendReceive
    val payload = MultipartFormData(Seq(BodyPart(new File("Pictures/icon.png"), "datafile", MediaTypes.`application/base64`)))
    val request =
      Post("http://localhost:8080/user/image", payload)

    pipeline(request).onComplete { res =>
      println(res)
      system.shutdown()
    }
  }

}
