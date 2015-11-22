import akka.actor._
//import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global

object FBClient {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    /*implicit val system2 = ActorSystem("FacebookClientSimulator")
    val t = system2.actorOf(Props(new MemberActor()), "TEST")
    system2.scheduler.scheduleOnce(10000 milliseconds, t, CreateUser("Chelsea", "Metcalf", Gender.Female))*/
  }
}