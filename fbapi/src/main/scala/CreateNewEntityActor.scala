import akka.actor.{ActorLogging, Actor, ActorRef}
import spray.http.StatusCodes._

class CreateNewEntityActor(keychain : ActorRef, identService : ActorRef, data : Create, key : Option[KeyMaterial])
  extends Actor with ActorLogging {
  // TODO: handle timeouts
  identService ! GetId()

  var id : Identifier = _

  def receive = {
    case id : Identifier =>
      this.id = id

      // does the entity have a key associated with it?
      if(key.isDefined) {
        keychain ! CreateKey(id, data.ctx.self, key.get)
      } else {
        finishEnt
      }
    // the new key has been added
    case KeychainSuccess() =>
      finishEnt
    case KeychainFailure =>
      log.error("Failed to create keychain for new object ID " + id.toString)
      data.ctx.req.complete(InternalServerError, "Failed to create keychain object for ID " + id.toString)
      context.stop(self)
  }

  def finishEnt = {
    context.parent ! EntCreated(data.ctx, id, data)
    context.stop(self)
  }
}