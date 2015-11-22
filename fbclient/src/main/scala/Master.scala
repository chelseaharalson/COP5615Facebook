import akka.actor._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class Master(implicit system: ActorSystem) extends Actor {

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
      var counter = 0
      for (iFN <- 0 until girlFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          counter = counter + 1
          val t = system.actorOf(Props(new MemberActor()), counter.toString)
          t ! CreateUser(girlFirstNames(iFN), lastNames(iLN), Gender.Female)
        }
      }

      for (iFN <- 0 until boyFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          counter = counter + 1
          val t = system.actorOf(Props(new MemberActor()), counter.toString)
          t ! CreateUser(boyFirstNames(iFN), lastNames(iLN), Gender.Male)
        }
      }

      Thread.sleep(5000)
      context.self ! AddFriends(counter)
    }

    case AddFriends(numOfUsers) => {
      for (i <- 1 to numOfUsers) {
        context.actorSelection("../" + i.toString()) ! AddFriends(numOfUsers)
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
