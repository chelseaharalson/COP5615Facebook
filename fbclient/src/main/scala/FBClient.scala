import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    val numOfUsers: Integer = 10
    val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    val Network = new Network()
    //println("Hello from Facebook Client!")
  }
}
