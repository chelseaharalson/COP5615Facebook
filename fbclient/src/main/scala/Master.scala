import akka.actor._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer


class Master extends Actor {

  // Users - large set
  /*val fileGirlNames = "TextFiles/GirlNames.txt"
  var girlFirstNames = parseFile(fileGirlNames)

  val fileBoyNames = "TextFiles/BoyNames.txt"
  var boyFirstNames = parseFile(fileBoyNames)

  val fileLastNames = "TextFiles/LastNames.txt"
  var lastNames = parseFile(fileLastNames)*/

  // Small sample - used for testing
  var girlFirstNames = Array("Emma",
    "Olivia"/*,
    "Sophia",
    "Isabella",
    "Ava",
    "Mia",
    "Emily",
    "Abigail",
    "Madison",
    "Charlotte"*/)

  var boyFirstNames = Array("Noah",
    "Liam"/*,
    "Mason",
    "Jacob",
    "William",
    "Ethan",
    "Michael",
    "Alexander",
    "James",
    "Daniel"*/)

  var lastNames = Array("Smith",
    "Johnson"/*,
    "Williams",
    "Jones",
    "Brown",
    "Davis",
    "Miller",
    "Wilson",
    "Moore",
    "Taylor"*/)

  def receive = {
    case CreateUsers => {
      println("Hi from master")
      val Network = new Network()
      for (iFN <- 0 until girlFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          val userActor = new UserActor(girlFirstNames(iFN), lastNames(iLN), Gender.Female)

          Network.addUser(userActor.firstName, userActor.lastName, userActor.birthday, userActor.gender,
            userActor.email, userActor.about, userActor.relationshipStatus,
            userActor.interestedIn, userActor.political, userActor.tz)

          //Network.getUser()
          /*Network.addUser("Chelsea", "Metcalf", DateTime.now, Gender.Female,
            "chelsea.metcalf@gmail.com", "Test about", RelationshipStatus.Single,
            Gender.Male, PoliticalAffiliation.Democrat, TimeZone.getDefault)*/

        }
      }

      for (iFN <- 0 until boyFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          val userActor = new UserActor(boyFirstNames(iFN), lastNames(iLN), Gender.Male)

          Network.addUser(userActor.firstName, userActor.lastName, userActor.birthday, userActor.gender,
            userActor.email, userActor.about, userActor.relationshipStatus,
            userActor.interestedIn, userActor.political, userActor.tz)
        }
      }
    }
  }

  def parseFile(fileName: String): ArrayBuffer[String] = {
    var rfile = ArrayBuffer[String]()
    for (line <- Source.fromFile(fileName).getLines()) {
      rfile += line
    }
    rfile
  }

}
