import akka.actor._
import java.security._
import java.util.Base64

object FBClient {
  def usage = {
    println("fbclient v1.0")
    println("usage: [hostname] [num users] [num pages] [load]")
    println(" hostname: a domain name")
    println(" num users: number of users to simulate")
    println(" num pages: number of pages to create")
    println(" load: time slider for content creation rate (must be >= 1)")
  }

  def main(args: Array[String]): Unit = {
    var numPages = 20
    var numUsers = 200
    var loadFactor = 1

    if(args.length == 0) {
      usage
      sys.exit()
    }

    if(args.length >= 1) {
      Network.Hostname = args(0)
    }

    if(args.length >= 2) {
      numUsers = args(1).toInt
    }

    if(args.length >= 3) {
      numPages = args(2).toInt
    }

    if(args.length >= 4) {
      loadFactor = args(3).toInt
    }

    if(loadFactor < 1) {
      println("error: load factor must be >= 1")
      sys.exit()
    }

    println("Target host: " + Network.HostURI)
    println(s"Testing with ${numUsers} users, ${numPages} pages, and a load of ${loadFactor}")

    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! InitMaster(numUsers, numPages, 1.0/loadFactor)

    //testSec()
  }

  def testSec() = {
    /*val rsa = new RSAhelper()
    val r = rsa.generateKeys()
    val pub_key = rsa.convertPublicKeyStr(r._1)
    val priv_key = r._2
    val aes = new AEShelper()
    val msg = "This is the test message."
    val triple = aes.encryptMessage(msg, pub_key)
    val strRSA = aes.decryptMessage(triple._1, priv_key, triple._2, triple._3)
    println(strRSA)*/

    val originalText = "Text to be encrypted "
    val rsa = new RSAhelper()
    val r = rsa.generateKeys()
    val pub = rsa.convertPublicKeyStr(r._1)
    val sig = rsa.generateSignature(r._2, originalText)
    rsa.verifySignature(r._1, sig, originalText)
  }
}