import akka.actor.{Actor, ActorSystem}
import spray.client.pipelining._
import spray.http.HttpRequest
import scala.concurrent.Future
import scala.util.{Failure, Success, Random}
import com.github.nscala_time.time.Imports._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

class Page(implicit system: ActorSystem) extends Actor {

  def receive = {
    case CreatePages => {
      for (i <- 0 until 20) {
        createPage()
      }
    }
  }

  def generatePageInformation(pageArr: ArrayBuffer[String]) : String = {
    var page: String = ""
    val i: Integer = Random.nextInt(pageArr.size)
    page = pageArr(i)
    page
  }

  def parseFile(fileName: String): ArrayBuffer[String] = {
    var pfile = ArrayBuffer[String]()
    for (line <- Source.fromFile(fileName).getLines()) {
      pfile += line
    }
    pfile
  }

  def createPage() = {
    val pageNamesFile = parseFile("TextFiles/PageNames.txt")
    val pageNames = generatePageInformation(pageNamesFile)
    val pageAboutsFile = parseFile("TextFiles/About.txt")
    val pageAbouts = generatePageInformation(pageAboutsFile)
    val pageBusinessFile = parseFile("TextFiles/Business.txt")
    val pageBusiness = generatePageInformation(pageBusinessFile)
    val pageContactAddressFile = parseFile("TextFiles/Address.txt")
    val pageContactAddress = generatePageInformation(pageContactAddressFile)
    val pageDescFile = parseFile("TextFiles/About.txt")
    val pageDesc = generatePageInformation(pageDescFile)
    val pageLocationFile = parseFile("TextFiles/Locations.txt")
    val pageLocation = generatePageInformation(pageLocationFile)
    val pagePhoneNumFile = parseFile("TextFiles/Phone.txt")
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
      addHeader("X-My-Special-Header", "fancy-value")
        ~> sendReceive
        ~> unmarshal[PageEnt]
      )

    val response: Future[PageEnt] =
      pipeline(Post("http://localhost:8080/page", PageCreateForm(name, about, business, contact_address,
        description, location, phone_number)))

    response onComplete{
      case Success(r) => {
        println("PAGE " + r.id)
      }
      case Failure(e) => e
    }

    response
  }

}
