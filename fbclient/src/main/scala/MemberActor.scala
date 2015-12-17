import java.security.PrivateKey
import java.util.Base64
import javax.crypto.SecretKey
import akka.actor.{ActorLogging, Actor, ActorSystem, Cancellable}
import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.{Failure, Success, Random}
//import scala.concurrent.ExecutionContext.Implicits.global

case class GrantKey(objId : Identifier, user : Identifier,
                    key : SecretKey, nonce : Array[Byte], sig : String)

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
    case GrantKey(objId, friend, key, nonce, sig) =>
      val aes = new AEShelper()
      val ourPublicKey = GlobalInfo.getPublicKey(ent.id)
      val friendPublicKey = GlobalInfo.getPublicKey(friend)
      val encKey = aes.encryptKey(key, friendPublicKey)
      val strNonce = Base64.getEncoder.encodeToString(nonce)

      import scala.concurrent.ExecutionContext.Implicits.global
      Network.addKey(ent.id, objId, friend, KeyMaterial(encKey, strNonce, sig)) onComplete {
        case Success(e) => context.actorSelection("../user" + friend) ! GetPost(objId, ourPublicKey)
        case Failure(e) => println("Failed to add key: " + e.getMessage)
      }
    case DoPost(content) => {
      val timePosted = System.currentTimeMillis()

      val post : String = content

      // choose random friend to post to
      val randFriend = Random.nextInt(friendList.friends.size-1)

      // get our ID and our friend's ID
      val s1 = ent.id.toString
      val s2 = friendList.friends(randFriend).toString

      // our public key
      val ourPublicKey = GlobalInfo.getPublicKey(ent.id)

      // we should have access to our pub key
      assert(!ourPublicKey.equals(""))

      // generate URL
      val uri = Network.HostURI + "/user/" + s1 + "/post/" + s2

      // 1. create new post
      // 2. generate new AES key
      // 3. encrypt post content with AES key
      // 4. encrypt AES key with creator's public key (us)
      // 5. sign post content with our private key
      // 6. upload post content and encrypted key

      val aes = new AEShelper()
      val (aesKey, aesNonce) = aes.generateKey()

      val (encryptedPost, encryptedKey, base64Nonce) = aes.encryptMessage(aesKey, aesNonce, post, ourPublicKey)

      // Generate digital signature
      val rsa = new RSAhelper()
      val public_key = rsa.getPublicKey(ourPublicKey)
      val postSignature = rsa.generateSignature(p_private_key, encryptedPost)
      val str_sig = Base64.getEncoder.encodeToString(postSignature)

      import scala.concurrent.ExecutionContext.Implicits.global
      Network.addPost(uri, encryptedPost, encryptedKey, base64Nonce, str_sig) onComplete{
        case Success(postent) =>
          println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Post ID: " + postent.entity.id + " to friend " + s2 + " from user " + s1)
          //context.actorSelection("../user" + s2) ! GetPost(postent.entity.id, public_key)

          context.self ! GrantKey(postent.entity.id, friendList.friends(randFriend), aesKey, aesNonce, str_sig)
          context.self ! GetPost(postent.entity.id, ourPublicKey)
        case Failure(e) =>
          println("Failed to add post: " + e.getMessage)
      }
      val rt = Random.nextInt(60000)
      schedulePosting(rt * loadConfig)
    }

    case GetPost(postId, public_key) => {

      val uri = Network.HostURI + "/post/" + ent.id + "/" + postId.toString
      Network.getPost(uri, private_key, public_key)
      println("@@@@@ Get Post " + postId.toString + " as user " + ent.first_name + " " + ent.last_name + " (" + ent.id + ")")
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
