import scala.util.Random
import akka.actor._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object MasterState extends Enumeration {
  type EnumVal = Value
  val Init, CreateUsers, CreatePages, LinkFriends, Running = Value
}

class Master(implicit system: ActorSystem) extends Actor with ActorLogging {
  var userIDlist = ArrayBuffer[Identifier]()
  var numUsers = 0
  var numPages = 0
  var girlsToBoys = 0.5
  var loadFactor : Double = _

  var masterState = MasterState.Init

  def advance = {
    masterState match {
      case MasterState.Init =>
        masterState = MasterState.CreateUsers

        context.actorOf(Props(new CreateUsersTask(numUsers, girlsToBoys, loadFactor)), "create-users")
      case MasterState.CreateUsers =>
        masterState = MasterState.CreatePages

        context.actorOf(Props(new CreatePagesTask(numPages)), "create-pages")
      case MasterState.CreatePages =>
        masterState = MasterState.LinkFriends

        context.self ! AddFriends()
      case MasterState.LinkFriends =>
        masterState = MasterState.Running
      case MasterState.Running =>
        masterState = MasterState.Running
      case _ => log.error("Unhandled state")
    }

    log.info("Tasking for state " + masterState)
  }

  def addFriends = {
    val rand = new Random()

    for (i <- userIDlist.indices) {
      var friendList = ArrayBuffer[Identifier]()
      var c = 0

      val amtOfFriends = rand.nextInt((userIDlist.size*0.4).toInt) + 2
      //println("Adding " + amtOfFriends + " for user " + userIDlist(i))

      do {
        val randFriend = rand.nextInt(userIDlist.size)

        // amt of friends must be greater than zero, we cannot add duplicate friends, and can't add our
        // selves
        if (amtOfFriends > 0 && !friendList.contains(userIDlist(randFriend)) && randFriend != i) {
          //println("Random Friend: " + randFriend + "  Amount of Friends: " + amtOfFriends)
          friendList += userIDlist(randFriend)
          c = c + 1
        }
      } while(c < amtOfFriends)

      //println(friendList)
      context.actorSelection("../user" + userIDlist(i).toString) ! AddFriendList(friendList)
    }
  }

  def receive = {
    case InitMaster(nUsers, nPages, lFactor) =>
      numUsers = nUsers
      numPages = nPages
      loadFactor = lFactor
      advance
    case TaskCompletion(res) =>
      if(!res) {
        log.error("Failed to complete task for state: " + masterState)
        context.stop(self)
      } else {
        advance
      }
    case AddFriends() =>
      addFriends
      self ! TaskCompletion(true)
    case AddID(userID) => {
      userIDlist += userID
    }
  }
}
