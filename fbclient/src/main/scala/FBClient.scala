import akka.actor._

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

    // TESTING KEY GENERATION
    /*val originalText = "Text to be encrypted "
    val rsa = new RSAhelper()
    val keymap = rsa.generateKeys()
    for ((key, value) <- keymap) {
      //println (key + "-->" + value)
      val cipherText = rsa.encrypt(originalText, key)
      val plainText = rsa.decrypt(cipherText, value)

      println("Original: " + originalText)
      println("Encrypted: " + cipherText.toString)
      println("Decrypted: " + plainText)
    }*/

  }
}