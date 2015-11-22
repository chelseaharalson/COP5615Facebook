import akka.actor.{Props, Cancellable, Actor, ActorSystem}
import spray.http.HttpRequest
import java.util.TimeZone
import spray.client.pipelining._
import scala.concurrent.Future
import com.github.nscala_time.time.Imports._
import scala.concurrent.duration._
//import scala.concurrent.ExecutionContext.Implicits.global

class MemberActor(implicit system: ActorSystem) extends Actor {
  var scheduler: Cancellable = _

  schedulePosting(20000)

  def receive = {
    case CreateUser(pFirstName, pLastName, pGender) => {
      createMember(pFirstName, pLastName, pGender)
    }

    case AddFriends(numOfUsers) => {
      println("Adding friends...")
    }
  }

  def addMember(first_name : String,
                last_name : String,
                birthday : DateTime,
                gender : Gender.EnumVal,
                email : String,
                about : String,
                relationship_status : RelationshipStatus.EnumVal,
                interested_in : Gender.EnumVal,
                political : PoliticalAffiliation.EnumVal,
                tz : TimeZone) : Future[UserEnt] = {
    import FacebookJsonSupport._
    import scala.concurrent.ExecutionContext.Implicits.global
    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )

    /*val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm("Chelsea", "Metcalf", DateTime.now, Gender.Female,
        "chelsea.metcalf@gmail.com", "Test about", RelationshipStatus.Single,
        Gender.Male, PoliticalAffiliation.Democrat, TimeZone.getDefault)))*/

    val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm(first_name, last_name, birthday, gender,
        email, about, relationship_status, interested_in, political, tz)))

    response
  }

  def createMember(pFirstName: String, pLastName: String, pGender: Gender.EnumVal) = {
    val user = new User()

    val fileAbout = "TextFiles/About.txt"
    val abouts = user.parseFile(fileAbout)

    val fileStatus = "TextFiles/Status.txt"
    val statuses = user.parseFile(fileStatus)

    val firstName: String = pFirstName
    val lastName: String = pLastName
    val birthday: DateTime = user.generateBirthday
    val gender: Gender.EnumVal = pGender
    val email: String = firstName + "." + lastName + "@gmail.com"
    val about: String = user.generateAbout(abouts)
    val relationshipStatus: RelationshipStatus.EnumVal = user.generateRelationshipStatus
    val status: String = ""
    val interestedIn: Gender.EnumVal = user.generateInterestedIn(gender)
    val political: PoliticalAffiliation.EnumVal = user.generatePoliticalStatus
    val last_updated: DateTime = DateTime.now
    val tz: TimeZone = TimeZone.getDefault

    addMember(firstName, lastName, birthday, gender, email, about, relationshipStatus, interestedIn, political, tz)
  }

  def schedulePosting(mili : Long) = {
    //scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(mili, MILLISECONDS), self, doPost(1, 2, "test post"))
    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! doPost(1, 2, "test post")
    }
  }

  def doPost(myID : Integer, friendID : Integer, content : String) = {
    /*import scala.concurrent.ExecutionContext.Implicits.global
    import FacebookJsonSupport._

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )*/

    println(content)

    /*val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm(content)))*/
  }

  def addFriends(numOfFriends : Int, numOfMembers : Int) = {

  }
}
