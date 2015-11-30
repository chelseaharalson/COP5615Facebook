import akka.actor.{ActorLogging, Actor, ActorSystem, Cancellable}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random
//import scala.concurrent.ExecutionContext.Implicits.global

class MemberActor(ent : UserEnt)(implicit system: ActorSystem) extends Actor with ActorLogging {
  var scheduler: Cancellable = _
  var loadConfig = 2

  var counter = 0
  var friendList = new FriendsList(mutable.MutableList[Identifier]())
  var randomTime = Random.nextInt(50000)
  var albumCount = 0
  var pictureCount = 0

  //schedulePosting((randomTime+20000) * loadConfig)
  //scheduleAlbumPosting((randomTime+30000) * loadConfig)
  //schedulePicturePosting((randomTime+30000) * loadConfig)

  override def preStart = {
    //log.info("Starting as " + context.self.path)
  }

  def receive = {
    case AddFriendList(userList) => {
      //println("FRIEND LIST : " + userList)
      friendList = new FriendsList(mutable.MutableList[Identifier](userList.toSeq : _*))
      //println("FRIEND LIST : " + friendList)

      //val s = new SendMessages()
      for (i <- 0 until friendList.friends.size) {
        val s1 = ent.id.toString
        val s2 = friendList.friends(i).toString
        //println("s1: " + s1 + "  s2: " + s2)
        Network.post("/user/" +s1+ "/add_friend/"+s2)
      }
    }

    case DoPost(content) => {
      val timePosted = System.currentTimeMillis()
      val r = Random.nextInt(friendList.friends.size-1)
      var post : String = content
      post = "User " + ent.id + " posted to " + friendList.friends(r) + " : " + content
      post = post.replaceAll(" ","%20")
      //val s = new SendMessages()
      val s1 = ent.id.toString
      val s2 = friendList.friends(r).toString
      Network.post("/user/add_post/"+s1+"/"+s2+"/"+post)
      //println(post)
      val rt = Random.nextInt(60000)
      schedulePosting(rt * loadConfig)
      import scala.concurrent.ExecutionContext.Implicits.global
      scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(rt, MILLISECONDS), self, DoPost(post))
    }

    case DoAlbum(albumName) => {
      var aName : String = albumName
      aName = aName.replaceAll(" ","%20")
      //val s = new SendMessages()
      val s1 = ent.id.toString
      val s2 = albumCount.toString
      val rt = Random.nextInt(100000)
      Network.send("/user/add_album/"+s1+"/"+s2+"/"+aName)
      scheduleAlbumPosting((rt+30000) * loadConfig)
      import scala.concurrent.ExecutionContext.Implicits.global
      scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(rt, MILLISECONDS), self, DoAlbum(aName))
    }

    case DoPicture() => {
      //val s = new SendMessages()
      val rt = Random.nextInt(100000)
      Network.uploadFile()
      schedulePicturePosting((rt+60000) * loadConfig)
      import scala.concurrent.ExecutionContext.Implicits.global
      scheduler = context.system.scheduler.scheduleOnce(new FiniteDuration(rt, MILLISECONDS), self, DoPicture())
    }

  }

  def schedulePosting(mili : Long) {
    val userPost = User.generateStatus

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoPost(userPost)
    }
  }

  def scheduleAlbumPosting(mili : Long) {
    val albumName = User.generateStatus
    albumCount = albumCount + 1

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoAlbum(albumName)
    }
  }

  def schedulePicturePosting(mili : Long) {
    pictureCount = pictureCount + 1

    import system.dispatcher
    system.scheduler.scheduleOnce(mili milliseconds) {
      self ! DoPicture()
    }
  }

}
