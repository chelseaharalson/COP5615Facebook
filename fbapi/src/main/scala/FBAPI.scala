import akka.actor.{ActorSystem, Props}

import FacebookJsonSupport._
import spray.json._

object FBAPI {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("fbapi")

    val port = 8080
    var hostname = "localhost"

    if (args.length >= 1) {
      hostname = args(0)
    }

    println(KeyedEnt(new PostEnt, KeyMaterial("thekey", "thenonce", "thesig")).toJson.prettyPrint)

    // bind to the host:port
    system.actorOf(Props(new HttpBinder(hostname, port)), "http-binder")
  }
}
