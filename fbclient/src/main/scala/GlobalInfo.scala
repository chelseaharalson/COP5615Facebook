import scala.collection.mutable.ListBuffer

case class FriendInfo(id : Identifier, public_key : String)

object GlobalInfo {

  var keychain = new ListBuffer[FriendInfo]()

  def getPublicKey(pId : Identifier) : String = {
    var rkey = ""
    keychain.foreach(kc => if (kc.id.equals(pId)) rkey = kc.public_key)
    rkey
  }

}
