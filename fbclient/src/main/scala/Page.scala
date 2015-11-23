import scala.util.Random
import com.github.nscala_time.time.Imports._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

class Page {

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

}
