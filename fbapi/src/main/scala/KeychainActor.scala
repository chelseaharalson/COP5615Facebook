import akka.actor.{ActorLogging, Actor}
import scala.collection.mutable
import scala.util.{Success, Failure}

case class CreateKey(objId : Identifier, userId : Identifier, key : KeyMaterial)
case class RevokeKey(objId : Identifier, userId : Identifier)
case class GetKey(objId : Identifier, userId : Identifier)
case class KeychainSuccess()
case class KeychainKey(key : KeyMaterial)
case class KeychainFailure()

class KeychainActor extends Actor with ActorLogging {
  // keychains for users
  var objectKeys = mutable.HashMap[Identifier, mutable.HashMap[Identifier, KeyMaterial]]()

  def receive = {
    case CreateKey(objId, user, key) => ;
      log.info(s"Creating key for ${objId} (user ${user})")
      createKey(objId, user, key)

      sender() ! KeychainSuccess()
    case RevokeKey(objId, user) => ;
      val ret = deleteKey(objId, user)

      if(ret.isDefined) {
        sender() ! KeychainKey(ret.get)
      } else {
        sender() ! KeychainFailure()
      }
    case GetKey(objId, user) =>
      val ret = getKey(objId, user)

      if(ret.isDefined) {
        sender() ! KeychainKey(ret.get)
      } else {
        sender() ! KeychainFailure()
      }
    case _ => log.error("Unknown message")
  }

  def keyExists(objId : Identifier, userId : Identifier) : Boolean = {
    if (!objectKeys.contains(objId)) {
      return false
    }

    objectKeys{objId}.contains(userId)
  }

  def getKey(objId : Identifier, userId : Identifier) : Option[KeyMaterial] = {
    if (!keyExists(objId, userId)) {
      return None
    }

    objectKeys{objId}.get(userId)
  }

  def deleteKey(objId : Identifier, userId : Identifier) : Option[KeyMaterial] = {
    if (!keyExists(objId, userId)) {
      return None
    }

    val key = getKey(objId, userId)
    objectKeys{objId}.remove(userId)

    if (objectKeys{objId}.isEmpty) {
      objectKeys.remove(objId)
    }

    key
  }

  def createKey(objId : Identifier, userId : Identifier, key : KeyMaterial) = {
    if (!objectKeys.contains(objId)) {
      objectKeys += (objId -> mutable.HashMap())
    }

    objectKeys{objId} += (userId -> key)
  }
}
