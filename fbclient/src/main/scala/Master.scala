import scala.util.Random
import akka.actor._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class Master(implicit system: ActorSystem) extends Actor {

  var userIDlist = ArrayBuffer[Identifier]()

  val numOfFriends = 10

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
      // TODO : fix random CM
      var amtOfFriends = 0

      var c = 0
      var friendList = ArrayBuffer[Identifier]()

      for (i <- 1 to numOfUsers) {
        friendList.clear()
        c = 0
        amtOfFriends = Math.abs(Random.nextInt(numOfFriends)) + 2
        //amtOfFriends = Math.random() * numOfFriends
        do {
          var randFriend = 0
          randFriend = Math.abs(Random.nextInt(amtOfFriends)) + 1
          //randFriend = Math.random() * amtOfFriends
          //randFriend = getRealRandom(amtOfFriends)
          if (userIDlist(randFriend) != null && amtOfFriends > 0 && !friendList.contains(userIDlist(randFriend))) {
            //println("Random Friend: " + randFriend + "  Amount of Friends: " + amtOfFriends)
            friendList.+=(userIDlist(randFriend))
            c = c + 1
          }
        } while(c < amtOfFriends);

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

  /*def getRealRandom(amt : Int) : Int = {
    var r = Random.nextInt(999)
    var r1 = 0.0
    println("BEFORE: " + r)
    r1 = ((r / 100) * amt)
    r = r1.toInt
    println(r1)
    r
  }*/

}
