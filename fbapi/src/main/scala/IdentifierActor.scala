import akka.actor.{ActorLogging, Actor}

case class GetId()
case class GetCurrentId()

class IdentifierActor extends Actor with ActorLogging {
  // Global ID counter
  var nextId = 0

  def receive = {
    case GetId() =>
      sender ! new Identifier(nextId)
      nextId += 1
    case GetCurrentId() =>
      sender ! new Identifier(nextId)
    case _ => log.error("Unknown message")
  }
}
