import java.security.PrivateKey
import java.util.Base64
import akka.actor.{ActorLogging, Actor, ActorSystem, Cancellable}
import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.{Failure, Success, Random}
//import scala.concurrent.ExecutionContext.Implicits.global

class MemberActor(ent : UserEnt, loadConfig : Double, p_private_key : PrivateKey)(implicit system: ActorSystem) extends Actor with ActorLogging {
  var scheduler: Cancellable = _

  var counter = 0
  var friendList = new FriendsList(mutable.MutableList[Identifier]())
  var randomTime = Random.nextInt(50000)
  var albumCount = 0
  var pictureCount = 0

  var private_key = p_private_key

  override def preStart = {
    //log.info("Starting as " + context.self.path)
  }

  def receive = {
    case AddFriendList(userList) => {
      //println("FRIEND LIST : " + userList)
      friendList = new FriendsList(mutable.MutableList[Identifier](userList.toSeq : _*))
      println("Friend list for " + ent.first_name + " " + ent.last_name + " (" + ent.id + "): " + friendList)

      for (i <- 0 until friendList.friends.size) {
        val s1 = ent.id.toString
        val s2 = friendList.friends(i).toString
        Network.post("/user/" +s1+ "/add_friend/"+s2)
      }

      if(friendList.friends.nonEmpty) {
        schedulePosting((randomTime + 20000) * loadConfig)
        scheduleAlbumPosting((randomTime + 30000) * loadConfig)
        schedulePicturePosting((randomTime + 30000) * loadConfig)
      }
    }

    case DoPost(content) => {
      val timePosted = System.currentTimeMillis()
      val r = Random.nextInt(friendList.friends.size-1)

      var post : String = content
      post = content
      val s1 = ent.id.toString
      val s2 = friendList.friends(r).toString
      val pub_key = GlobalInfo.getPublicKey(friendList.friends(r))
      if (!pub_key.equals("")) {
        val uri = Network.HostURI + "/user/"+s1+"/post/"+s2

        val aes = new AEShelper()
        val triple = aes.encryptMessage(post, pub_key)

        // Generate digital signature
        val rsa = new RSAhelper()
        val r = rsa.generateKeys()
        val public_key = rsa.convertPublicKeyStr(r._1)
        val sig = rsa.generateSignature(r._2, triple._1)
        val str_sig = Base64.getEncoder.encodeToString(sig)

        import scala.concurrent.ExecutionContext.Implicits.global
        Network.addPost(uri, triple._1, triple._2, triple._3, str_sig) onComplete{
          case Success(postent) =>
            //println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ POST ID: " + postent.id + " to friend " + s2)
            context.actorSelection("../user" + s2) ! GetPost(postent.id, public_key)

          case Failure(e) =>
            println("Failed to add post!")
        }
      }
      val rt = Random.nextInt(60000)
      schedulePosting(rt * loadConfig)
    }

    case GetPost(postId,public_key) => {
      val uri = Network.HostURI + "/post/" + postId.toString
      Network.getPost(uri, private_key, public_key)
      //println("Received " + postId.toString + "          " + ent.id.toString)
    }

    case DoAlbum(albumName,albumDescription) => {
      val s1 = ent.id.toString
      //val s2 = albumCount.toString
      val rt = Random.nextInt(100000)
      val uri = Network.HostURI + "/album/"+s1
      Network.addAlbum(uri,albumName,albumDescription)
      scheduleAlbumPosting((rt+30000) * loadConfig)
      //import scala.concurrent.ExecutionContext.Implicits.global
      //scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(rt, MILLISECONDS), self, DoAlbum(aName))
    }

    case DoPicture(caption,fileId) => {
      val rt = Random.nextInt(100000)
      val albumId = new Identifier(albumCount)
      //Network.uploadFile()
      val uri = Network.HostURI + "/picture/"+albumId.toString
      Network.addPicture(uri,caption,fileId)
      schedulePicturePosting((rt+60000) * loadConfig)
      //import scala.concurrent.ExecutionContext.Implicits.global
      //scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(rt, MILLISECONDS), self, DoPicture())
    }

  }

  def schedulePosting(mili : Long) {
    val userPost = User.generateStatus

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoPost(userPost)
    }
  }

  def schedulePosting(mili : Double) : Unit = schedulePosting(mili.toLong)

  def scheduleAlbumPosting(mili : Long) {
    val albumName = User.generateStatus
    val albumDescription = User.generateDesc
    albumCount = albumCount + 1

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoAlbum(albumName,albumDescription)
    }
  }

  def scheduleAlbumPosting(mili : Double) : Unit = scheduleAlbumPosting(mili.toLong)

  def schedulePicturePosting(mili : Long) {
    val caption = User.generateStatus
    pictureCount = pictureCount + 1
    val fileId = new Identifier(pictureCount)

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoPicture(caption,fileId)
    }
  }

  def schedulePicturePosting(mili : Double) : Unit = schedulePicturePosting(mili.toLong)

}
