import akka.actor.Actor
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class UserActor extends Actor {

  var numOfFriends : Int = Random.nextInt(300)
  var numOfPosts : Int = Random.nextInt(300)
  var numOfAlbums : Int = Random.nextInt(20)
  var numOfPhotos : Int = Random.nextInt(300)

  def receive = {
    case MakeFriends => {

    }
    case StartActivities => {

    }
  }

  def addFriend = {

  }

  def addPostToFriend = {

  }

  def addStatus = {

  }

  def addAlbum = {

  }

  def addPhoto = {

  }

}
