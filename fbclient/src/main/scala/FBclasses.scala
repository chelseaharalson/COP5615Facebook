import scala.collection.mutable.ArrayBuffer

case class CreateUser(pFirstName : String, pLastName : String, pGender : Gender.EnumVal)
case class CreateUsers()
case class AddFriends(numOfUsers : Int)
case class AddFriendList(userList : ArrayBuffer[Identifier])
case class StartActivities()
case class CreatePage(pPageName : String)
case class AddID(userID : Identifier)