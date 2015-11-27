import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
  }

}