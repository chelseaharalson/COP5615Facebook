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
    "Olivia",
    "Sophia",
    "Isabella",
    "Ava",
    "Mia",
    "Emily",
    "Abigail",
    "Madison",
    "Charlotte")

  var boyFirstNames = Array("Noah",
    "Liam",
    "Mason",
    "Jacob",
    "William",
    "Ethan",
    "Michael",
    "Alexander",
    "James",
    "Daniel")

  var lastNames = Array("Smith",
    "Johnson",
    "Williams",
    "Jones",
    "Brown",
    "Davis",
    "Miller",
    "Wilson",
    "Moore",
    "Taylor")

  def receive = {
    case CreateUsers => {
      println("Hi from master")
      for (iFN <- 0 until girlFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          //println(girlFirstNames(iFN) + " " + lastNames(iLN))
          context.actorOf(Props(new UserActor(girlFirstNames(iFN), lastNames(iLN), "Female")), girlFirstNames(iFN) + lastNames(iLN))
        }
      }

      for (iFN <- 0 until boyFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          //println(girlFirstNames(iFN) + " " + lastNames(iLN))
          context.actorOf(Props(new UserActor(boyFirstNames(iFN), lastNames(iLN), "Male")), boyFirstNames(iFN) + lastNames(iLN))
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
