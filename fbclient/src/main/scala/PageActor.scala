import akka.actor.{Props, Cancellable, Actor, ActorSystem}
import spray.http.HttpRequest
import java.util.TimeZone
import spray.client.pipelining._
import scala.concurrent.Future
import com.github.nscala_time.time.Imports._
import scala.concurrent.duration._

class PageActor(implicit system: ActorSystem) extends Actor {

  def receive = {
    case CreatePage(pPageName) => {

    }
  }

  def createPage(pPageName : String) = {
    val page = new Page()
    val pageNamesFile = page.parseFile("TextFiles/PageNames.txt")
    val pageNames = page.generatePageInformation(pageNamesFile)
    val pageAboutsFile = page.parseFile("TextFiles/About.txt")
    val pageAbouts = page.generatePageInformation(pageAboutsFile)
    val pageBusinessFile = page.parseFile("TextFiles/Business.txt")
    val pageBusiness = page.generatePageInformation(pageBusinessFile)
    val pageContactAddressFile = page.parseFile("TextFiles/Address.txt")
    val pageContactAddress = page.generatePageInformation(pageContactAddressFile)
    val pageDescFile = page.parseFile("TextFiles/About.txt")
    val pageDesc = page.generatePageInformation(pageDescFile)
    val pageLocationFile = page.parseFile("TextFiles/Locations.txt")
    val pageLocation = page.generatePageInformation(pageLocationFile)
    val pagePhoneNumFile = page.parseFile("TextFiles/Phone.txt")
    val pagePhoneNum = page.generatePageInformation(pagePhoneNumFile)
  }

}
