//import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    //val s = new SendMessages()
    //s.send("/user/add_friendx/1/2")
  }

}