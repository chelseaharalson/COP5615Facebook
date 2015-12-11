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
    // TESTING KEY GENERATION
    /*val originalText = "Text to be encrypted "
    val rsa = new RSAhelper()
    val r = rsa.generateKeys()
    val pub = rsa.convertPublicKeyStr(r._1)
    val sig = rsa.generateSignature(r._2, originalText)
    rsa.verifySignature(r._1, sig, originalText)*/

    //println(rsa.getPublicKey(pub))
    //println(pub)
    //println(r._1)

    /*val cipherText = rsa.encrypt(originalText, r._1)
    val plainText = rsa.decrypt(cipherText, r._2)
    println("Original: " + originalText)
    println("Encrypted: " + cipherText.toString)
    println("Decrypted: " + plainText)*/

    //Security.addProvider(new BouncyCastleProvider())
    /*val text = "this is the input text"
    var encripted = Array[Byte]()
    println("input:\n" + text)
    encripted = rsa.encrypt(text, r._1)
    println("cipher:\n" + rsa.convertToBase64(encripted))
    //println("cipher:\n" + Base64.getEncoder.encodeToString((encripted)))
    println("decrypt:\n" + rsa.decrypt(encripted, r._2))*/

    val aes = new AEShelper()
    val originalText = "This is test text."
    val key = aes.generateKey()._1
    val nonce = aes.generateKey()._2
    val e = aes.encrypt(key, nonce, originalText)
    val s = aes.decrypt(key, nonce, e)
    println("Result: " + s)
  }
}