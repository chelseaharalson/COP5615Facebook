import java.util.concurrent.TimeUnit

import akka.actor.{Cancellable, ActorLogging, Actor, ActorSystem}
import spray.client.pipelining._
import spray.http.HttpRequest
import scala.concurrent.Future
import scala.util.{Failure, Success, Random}
import com.github.nscala_time.time.Imports._
import scala.concurrent.duration.Duration
import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

import ExecutionContext.Implicits.global

class CreatePagesTask(numPages : Int)(implicit system: ActorSystem) extends Actor with ActorLogging {
  val pageNamesFile = FBUtil.parseFile("TextFiles/PageNames.txt")
  val pageAboutsFile = FBUtil.parseFile("TextFiles/About.txt")
  val pageBusinessFile = FBUtil.parseFile("TextFiles/Business.txt")
  val pageContactAddressFile = FBUtil.parseFile("TextFiles/Address.txt")
  val pageDescFile = FBUtil.parseFile("TextFiles/About.txt")
  val pageLocationFile = FBUtil.parseFile("TextFiles/Locations.txt")
  val pagePhoneNumFile = FBUtil.parseFile("TextFiles/Phone.txt")

  var cancellable : Cancellable = null
  var numCreated = 0
  val rand = new Random()

  case class PageCreated(page : PageEnt)

  override def preStart = {
    context.system.scheduler.scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS), context.self, new StartTask())
    // timeout
    cancellable = context.system.scheduler.scheduleOnce(Duration.create(5, TimeUnit.SECONDS), context.self, new TimedOut())
  }

  def receive = {
    case StartTask() =>
      for (i <- 0 until numPages) {
        createPage()
      }
    case PageCreated(ent) =>
      numCreated += 1

      if(numCreated == numPages) {
        log.info("Created " + numPages + " pages")
        context.parent ! TaskCompletion(true)

        cancellable.cancel()
        context.stop(self)
      }
    case TimedOut() =>
      log.error("Page creation timed out")

      context.parent ! TaskCompletion(false)
      context.stop(self)
    case _ => log.error("Unknown message")
  }

  def generatePageInformation(pageArr: ArrayBuffer[String]) : String = {
    var page: String = ""
    val i: Integer = rand.nextInt(pageArr.size)
    page = pageArr(i)
    page
  }

  def createPage() = {
    val pageNames = generatePageInformation(pageNamesFile)
    val pageAbouts = generatePageInformation(pageAboutsFile)
    val pageBusiness = generatePageInformation(pageBusinessFile)
    val pageContactAddress = generatePageInformation(pageContactAddressFile)
    val pageDesc = generatePageInformation(pageDescFile)
    val pageLocation = generatePageInformation(pageLocationFile)
    val pagePhoneNum = generatePageInformation(pagePhoneNumFile)

    addPage(pageNames, pageAbouts, pageBusiness, pageContactAddress, pageDesc, pageLocation, pagePhoneNum)
  }

  def addPage(name : String,
              about : String,
              business : String,
              contact_address : String,
              description : String,
              location : String,
              phone_number : String) : Future[PageEnt] = {

    import FacebookJsonSupport._
    import scala.concurrent.ExecutionContext.Implicits.global

    val pipeline: HttpRequest => Future[PageEnt] = (
        sendReceive
        ~> unmarshal[PageEnt]
      )

    val response: Future[PageEnt] =
      pipeline(Post(Network.HostURI + "/page", PageCreateForm(name, about, business, contact_address,
        description, location, phone_number)))

    response onComplete{
      case Success(ent) => {
        context.self ! PageCreated(ent)
      }
      case Failure(e) =>
        log.error("Failed to create page!")
    }

    response
  }

}
