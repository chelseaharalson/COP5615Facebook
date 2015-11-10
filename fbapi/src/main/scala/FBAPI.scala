import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import ExecutionContext.Implicits.global

object FBAPI {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("fbapi")
    val service = system.actorOf(Props[API], "api-service")

    // IO requires an implicit ActorSystem, and ? requires an implicit timeout
    // Bind HTTP to the specified service.
    implicit val timeout = Timeout(5.seconds)
    (IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)).mapTo[Http.Event].map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println(s"REST interface could not bind")
        system.shutdown()
    }
  }
}
