import akka.actor._
import spray.can.Http
import akka.io.IO
import spray.can.server.Stats
import scala.concurrent.duration._

import scala.concurrent.duration.FiniteDuration

class HttpBinder(hostname : String, port : Int)(implicit system : ActorSystem) extends Actor with ActorLogging {
  val service = system.actorOf(Props[API], "api-service")
  IO(Http) ! Http.Bind(service, interface = hostname, port = port)
  var listener : ActorRef = _

  val frequency = new FiniteDuration(1000, MILLISECONDS)


  def receive = {
    case Http.Bound(address) =>
      listener = sender()

      log.info(s"REST interface bound to $address")

      //import scala.concurrent.ExecutionContext.Implicits.global
      //system.scheduler.schedule(frequency, frequency, listener, Http.GetStats)
    case Http.CommandFailed(cmd) =>
      log.error(s"REST interface could not bind")
      system.shutdown()
    case s : Stats =>
      //log.info(s"Stats: openRequests ${s.totalRequests}")
    case x : Any => log.error("Unknown message: " + x.toString)
  }
}
