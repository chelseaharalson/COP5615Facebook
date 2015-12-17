import java.security.PublicKey

import scala.collection.mutable.ListBuffer

case class FriendInfo(id : Identifier, public_key : String)

object GlobalInfo {

  private var keychain = new ListBuffer[FriendInfo]()

  def getPublicKey(pId : Identifier) : String = {
    this.synchronized {
      var rkey = ""
      keychain.foreach(kc => if (kc.id.equals(pId)) rkey = kc.public_key)
      rkey
    }
  }

  def addPublicKey(pId: Identifier, public_key : String) = {
    this.synchronized {
      keychain += new FriendInfo(pId, public_key)
    }
  }

}
