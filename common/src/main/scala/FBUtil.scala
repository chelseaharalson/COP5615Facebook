import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object FBUtil {
  def parseFile(fileName: String): ArrayBuffer[String] = {
    var rfile = ArrayBuffer[String]()
    for (line <- Source.fromFile(fileName).getLines()) {
      rfile += line
    }
    rfile
  }
}
