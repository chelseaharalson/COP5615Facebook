import java.util.TimeZone
import java.util.concurrent.TimeUnit

import scala.util.{Success,Failure}
import akka.actor._
import org.joda.time.DateTime
import spray.http.HttpRequest
import spray.client.pipelining._
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.io.Source

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.concurrent.ExecutionContext

import ExecutionContext.Implicits.global

class CreateUsersTask(numUsers : Int, girlsToBoysRatio : Double, loadFactor : Double)(implicit system : ActorSystem)
  extends Actor with ActorLogging {
  case class UserCreated(user : UserEnt)

  assert(girlsToBoysRatio <= 1.0 && girlsToBoysRatio >= 0.0)

  val fileGirlNames = "TextFiles/GirlNames.txt"
  var girlFirstNames = FBUtil.parseFile(fileGirlNames)

  val fileBoyNames = "TextFiles/BoyNames.txt"
  var boyFirstNames = FBUtil.parseFile(fileBoyNames)

  val fileLastNames = "TextFiles/LastNames.txt"
  var lastNames = FBUtil.parseFile(fileLastNames)

  val fileAbout = "TextFiles/About.txt"
  val abouts = FBUtil.parseFile(fileAbout)

  val fileStatus = "TextFiles/Status.txt"
  val statuses = FBUtil.parseFile(fileStatus)

  var cancellable : Cancellable = null
  var numCreated = 0

  override def preStart = {
    context.system.scheduler.scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS), context.self, new StartTask())
    // timeout
    //cancellable = context.system.scheduler.scheduleOnce(Duration.create(5, TimeUnit.SECONDS), context.self, new TimedOut())
  }

  def receive = {
    case StartTask() =>
      createUsers
    case UserCreated(ent) =>
      numCreated += 1

      if(numCreated == numUsers) {
        log.info("Created " + numUsers + " users")
        context.parent ! TaskCompletion(true)

        //cancellable.cancel()
        context.stop(self)
      }
    case TimedOut() =>
      log.error("User creation timed out")

      context.parent ! TaskCompletion(false)
      context.stop(self)
    case _ => log.error("Unknown message")
  }

  def createUsers = {
    val rand = new Random()
    var counter = 0
    val numGirls = (numUsers * girlsToBoysRatio).toInt
    val numBoys = numUsers - numGirls

    for (iFN <- 0 until numGirls) {
      counter = counter + 1

      val first = girlFirstNames(rand.nextInt(girlFirstNames.size))
      val last = lastNames(rand.nextInt(lastNames.size))
      val form = createMemberForm(first, last, Gender.Female)

      addMember(form) onComplete{
        case Success(ent) =>
          system.actorOf(Props(new MemberActor(ent, loadFactor)), "user" + ent.id)

          context.self ! UserCreated(ent)
          context.parent ! AddID(ent.id)
        case Failure(e) =>
          log.error("Failed to create user!")
      }
    }

    for (i <- 0 until numBoys) {
      val first = boyFirstNames(rand.nextInt(boyFirstNames.size))
      val last = lastNames(rand.nextInt(lastNames.size))
      val form = createMemberForm(first, last, Gender.Male)

      addMember(form) onComplete{
        case Success(ent) =>
          system.actorOf(Props(new MemberActor(ent, loadFactor)), "user" + ent.id)

          context.self ! UserCreated(ent)
          context.parent ! AddID(ent.id)
        case Failure(e) =>
          log.error("Failed to create user!")
      }
    }
  }

  def addMember(form : UserCreateForm) : Future[UserEnt] = {
    import FacebookJsonSupport._

    import scala.concurrent.ExecutionContext.Implicits.global
    val pipeline: HttpRequest => Future[UserEnt] = (
        sendReceive
        ~> unmarshal[UserEnt]
      )

    val response: Future[UserEnt] =
      pipeline(Post(Network.HostURI + "/user", form))

    response
  }

  def createMemberForm(pFirstName: String, pLastName: String, pGender: Gender.EnumVal) = {
    val firstName: String = pFirstName
    val lastName: String = pLastName
    val birthday: DateTime = User.generateBirthday
    val gender: Gender.EnumVal = pGender
    val email: String = firstName + "." + lastName + "@gmail.com"
    val about: String = User.generateAbout(abouts)
    val relationshipStatus: RelationshipStatus.EnumVal = User.generateRelationshipStatus
    val status: String = ""
    val interestedIn: Gender.EnumVal = User.generateInterestedIn(gender)
    val political: PoliticalAffiliation.EnumVal = User.generatePoliticalStatus
    val last_updated: DateTime = DateTime.now
    val tz: TimeZone = TimeZone.getDefault

    UserCreateForm(firstName, lastName, birthday, gender, email, about, relationshipStatus,
      interestedIn, political, tz)
  }
}
