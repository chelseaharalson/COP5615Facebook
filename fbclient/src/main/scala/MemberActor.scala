import akka.actor.{Props, Cancellable, Actor, ActorSystem}
import spray.http.HttpRequest
import java.util.TimeZone
import spray.client.pipelining._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.nscala_time.time.Imports._

class MemberActor(implicit system: ActorSystem) extends Actor {
  var scheduler: Cancellable = _

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

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )

    /*val response: Future[UserEnt2] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm2("Chelsea", "Metcalf", Gender.Female,
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

  def waitForPost(mili : Long) = {
    implicit val system2 = ActorSystem("FacebookClientSimulator")
    //system2.scheduler.scheduleOnce(mili milliseconds, t, CreateUser("Chelsea", "Metcalf", Gender.Female))
  }

  def doPost(myID : Integer, friendID : Integer, post : String) = {
    import FacebookJsonSupport._

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )

    /*val response: Future[UserEnt2] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm2(first_name, last_name, gender,
        email, about, relationship_status, interested_in, political, tz)))*/

  }

  def addFriends(numOfFriends : Int, numOfMembers : Int) = {

  }
}
