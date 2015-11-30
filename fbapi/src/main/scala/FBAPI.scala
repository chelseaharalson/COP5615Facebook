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

    val port = 8080
    var hostname = "localhost"

    if (args.length >= 1) {
      hostname = args(0)
    }

    // bind to the host:port
    system.actorOf(Props(new HttpBinder(hostname, port)), "http-binder")
  }
}
