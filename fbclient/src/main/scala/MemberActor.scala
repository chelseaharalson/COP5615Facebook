import akka.actor.{Props, Cancellable, Actor, ActorSystem}
import spray.http.HttpRequest
import java.util.TimeZone
import spray.client.pipelining._
import scala.concurrent.Future
import com.github.nscala_time.time.Imports._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MemberActor(implicit system: ActorSystem) extends Actor {
  var scheduler: Cancellable = _
  //implicit val sys: ActorSystem = system

  //schedulePosting(10000)

  def receive = {
    case CreateUser(pFirstName, pLastName, pGender) => {
      createMember(pFirstName, pLastName, pGender)
    }

    case AddFriends(numOfUsers) => {
      println("Adding friends...")
    }
  }

  schedulePosting(10000)

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
    //implicit val system3 = ActorSystem("FBClientSimulator")
    //val t = system3.actorOf(Props(new MemberActor()), "TEST")
    //system3.scheduler.scheduleOnce(10000 milliseconds, context.self, doPost(1, 2, "test post"))

    scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(mili, MILLISECONDS), self, doPost(1, 2, "test post"))

    //scheduler = system3.scheduler.scheduleOnce(mili milliseconds, self, doPost(1, 2, "test post"))

    //sys.scheduler.scheduleOnce(10000 milliseconds, self, doPost(1, 2, "test post"))

    //val system = ActorSystem("MySystem")
    //system.scheduler.schedule(mili milliseconds)
    //system.scheduler.schedule(0 seconds, 5 minutes)(println("do something"))
  }

  def doPost(myID : Integer, friendID : Integer, post : String) = {
    /*import FacebookJsonSupport._

    val pipeline: HttpRequest => Future[UserEnt] = (
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[UserEnt]
      )*/

    println(post)

    /*val response: Future[UserEnt] =
      pipeline(Post("http://localhost:8080/user", UserCreateForm2(first_name, last_name, birthday, gender,
        email, about, relationship_status, interested_in, political, tz)))*/

  }

  def addFriends(numOfFriends : Int, numOfMembers : Int) = {

  }
}
