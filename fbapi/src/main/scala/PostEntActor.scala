import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import spray.routing.RequestContext

import scala.collection.mutable
import spray.http.StatusCodes._
import FacebookJsonSupport._
import spray.json._

class PostEntActor(identService : ActorRef, keychain : ActorRef) extends Actor with ActorLogging {
  var postMap = mutable.HashMap[Identifier, PostEnt]()
  def receive = {
    case x : Get =>
      context.actorOf(Props(new GetEntityActor(keychain, x)))
    case x : Create =>
      val key = x.data.asInstanceOf[PostCreateForm].key
      context.actorOf(Props(new CreateNewEntityActor(keychain, identService, x, Option(key))))
    // called when an entity key has successfully been found
    case EntKey(ctx, id, key) =>
      if (postMap.contains(id)) {
        ctx.req.complete(KeyedEnt(postMap{id}, key))
      } else {
        ctx.req.complete("Unknown Post ID")
      }
    // called when a new entity ID was captured. create is an object so it must be casted
    case EntCreated(ctx, id, create) =>
      val post = create.data.asInstanceOf[PostCreateForm]

      val ent = new PostEnt(id,
        owner = ctx.self,
        target = create.target,
        content = post.content
      )

      log.info("Creating post from user " + ctx.self + " to user " + create.target)
      postMap += (id -> ent)

      ctx.req.complete(OK, KeyedEnt(ent, post.key))
    case _ => log.error("Unknown message")
  }
}
