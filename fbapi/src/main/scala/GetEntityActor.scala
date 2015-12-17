import akka.actor.{Actor, ActorRef}
import spray.http.StatusCodes._

class GetEntityActor(keychain : ActorRef, get : Get) extends Actor {
  // TODO: handle timeouts
  keychain ! GetKey(get.id, get.ctx.self)

  def receive = {
    case KeychainKey(key) =>
      context.parent ! EntKey(get.ctx, get.id, key)
      context.stop(self)
    case KeychainFailure() =>
      get.ctx.req.complete(Forbidden, "You do not have keychain access to this object")
      context.stop(self)
  }
}