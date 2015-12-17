import scala.collection.mutable
import util.control.Breaks._

class FacebookACL() {

  def canAccess(ownerId : Identifier, privacy : Privacy.EnumVal, userMap : mutable.HashMap[Identifier, UserEnt],
                friendList : mutable.HashMap[Identifier, FriendsList]) : Boolean = {

    var accessGranted = false

    if (privacy == Privacy.OnlyMe) {
      breakable {
        for ((userId, userEntInfo) <- userMap) {
          if (userId == ownerId) {
            accessGranted = true
            break
          }
          else if (userId != ownerId) {
            accessGranted = false
          }
        }
      }
    }
    else if (privacy == Privacy.Friends) {
      if (friendList.contains(ownerId)) {
        accessGranted = true
      }
      else {
        println("User denied permission because not friend!")
        accessGranted = false
      }
    }
    else if (privacy == Privacy.Public) {
      accessGranted = true
    }
    accessGranted
  }
}
