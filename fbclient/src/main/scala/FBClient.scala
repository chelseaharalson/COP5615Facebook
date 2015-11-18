import akka.actor._

object FBClient {
  def main(args: Array[String]): Unit = {
    val numOfUsers: Integer = 10
    val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    //val Network = new Network()
    //Network.addUser()
    //println("Hello from Facebook Client!")
    /*Network.addUser("Chelsea", "Metcalf", DateTime.now, Gender.Female,
      "chelsea.metcalf@gmail.com", "Test about", RelationshipStatus.Single,
      Gender.Male, PoliticalAffiliation.Democrat, TimeZone.getDefault)*/
  }
}
