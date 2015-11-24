import scala.util.Random
import akka.actor._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class Master(implicit system: ActorSystem) extends Actor {

  var userIDlist = ArrayBuffer[Identifier]()

  val numOfFriends = 20

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
      var r = 0
      var r2 = 0
      var c = 0
      var friendList = ArrayBuffer[Identifier]()

      for (i <- 1 to numOfUsers) {
        friendList.clear()
        c = 0
        r = Random.nextInt(numOfFriends)
        r = r + 2
        do {
          r2 = Random.nextInt(r)
          if (!userIDlist(r2).equals(null) && !friendList.contains(userIDlist(r2))) {
            friendList.+=(userIDlist(r2))
            c = c + 1
          }
        } while(c < r);

        context.actorSelection("../" + i.toString()) ! AddFriendList(friendList)
      }
    }

    case AddID(userID) => {
      //println("ADDING ID...")
      userIDlist.+=(userID)
      //println(userIDlist)
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
