import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import spray.routing.RequestContext

import scala.collection.mutable
import spray.http.StatusCodes._
import FacebookJsonSupport._
import spray.json._

class AlbumEntActor(identService : ActorRef, keychain : ActorRef) extends Actor with ActorLogging {
  var albumMap = mutable.HashMap[Identifier, AlbumEnt]()
  def receive = {
    case x : Get =>
      context.actorOf(Props(new GetEntityActor(keychain, x)))
    case x : Create =>
      val key = x.data.asInstanceOf[AlbumCreateForm].key
      context.actorOf(Props(new CreateNewEntityActor(keychain, identService, x, Option(key))))
    // called when an entity key has successfully been found
    case EntKey(ctx, id, key) =>
      if (albumMap.contains(id)) {
        ctx.req.complete(KeyedEnt(albumMap{id}, key))
      } else {
        ctx.req.complete("Unknown Post ID")
      }
    // called when a new entity ID was captured. create is an object so it must be casted
    case EntCreated(ctx, id, create) =>
      val album = create.data.asInstanceOf[AlbumCreateForm]

      val ent = new AlbumEnt(id,
        owner = ctx.self,
        name = ,
        description = album
      )

      log.info("Creating album for user " + ctx.self)
      albumMap += (id -> ent)

      ctx.req.complete(OK, KeyedEnt(ent, album.key))
    case _ => log.error("Unknown message")
  }
}
