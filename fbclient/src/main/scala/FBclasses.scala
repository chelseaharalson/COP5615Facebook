import scala.collection.mutable.ArrayBuffer

case class InitMaster(numUsers : Int, numPages : Int, loadFactor : Double)

case class CreateUser(pFirstName : String, pLastName : String, pGender : Gender.EnumVal)
case class AddFriends()
case class AddFriendList(userList : ArrayBuffer[Identifier])
case class StartActivities()
case class CreatePages()
case class CreatePage(pPageName : String)
case class AddID(userID : Identifier)

case class TaskCompletion(result : Boolean)
case class StartTask()
case class TimedOut()

case class DoPost(content : String)
case class DoAlbum(albumName : String, albumDescription : String)
case class DoPicture(caption : String, fileId : Identifier)

case class GetPost(postId : Identifier, public_key : String)