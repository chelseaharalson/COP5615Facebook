import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    val numPages = 20
    val numUsers = 200

    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! InitMaster(numUsers, numPages)
  }
}