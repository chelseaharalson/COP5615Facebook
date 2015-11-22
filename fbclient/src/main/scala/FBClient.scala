import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    //system2.scheduler.scheduleOnce(1000 milliseconds, t, CreateUser("Chelsea", "Metcalf", Gender.Female))
  }
}