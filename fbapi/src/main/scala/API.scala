import java.io.{FileOutputStream, File}
import java.util.TimeZone
import akka.util.Timeout
import spray.http.MultipartFormData
import spray.http.StatusCodes.Unauthorized
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
// DO NOT remove this execution ctx as it is needed by Spray session deep down
import ExecutionContext.Implicits.global
import com.github.nscala_time.time.Imports.DateTime
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory
import spray.routing._
import session._
import session.directives._

import FacebookJsonSupport._


import scala.collection.mutable

case class Session(id : String, values : Map[String, Int]) {
  def getCurrentUser() = {
    val uid : Option[Int] = values.get("user_id")

    if(!uid.contains()) {
      throw new Exception("Invalid user ID")
    }

    new Identifier(uid.get)
  }

  def setUserId(id : Identifier) = {
    values.updated("user_id", id.asInt)
    values
  }
}

// simple actor that handles the routes.
class API extends Actor with HttpService with StatefulSessionManagerDirectives[Int] {

  // required as implicit value for the HttpService
  // included from SJService
  implicit val actorRefFactory = context.system
  //implicit val system : ActorSystem = actorRefFactory.system


  var objectActor : ActorRef = null

  override def preStart = {
    // create the data object actor
    objectActor = actorRefFactory.actorOf(Props[DataObjectActor], "DataObjectActor")
  }

  var map = mutable.HashMap[Identifier, UserEnt]()

  // we don't create a receive function ourselves, but use
  // the runRoute function from the HttpService to create
  // one for us, based on the supplied routes.
  def receive = runRoute(sessionRoute)

  // Path directive that extracts a Facebook ID
  def ObjectID = pathPrefix(FBID)

  val invalidSessionHandler = RejectionHandler {
    case InvalidSessionRejection(id) :: _ =>
      complete((Unauthorized, s"Unknown session $id"))
  }

  // needed as an implicit for Session timeout. The library does not currently use this
  implicit val timeout = Timeout(5.seconds)
  implicit val manager = new InMemorySessionManager[Int](ConfigFactory.load())

  lazy val sessionRoute = {
    handleRejections(invalidSessionHandler) {
      cookieSession() { (session_id, session_map) =>
        routes(Session(session_id, session_map))
      }
    }
  }

  // handles the other path, we could also define these in separate files
  def routes(session : Session) =
    pathPrefix("user") {
      path("test") {
        get {
          complete {
            new UserEnt()
          }
        }
      } ~
      ObjectID { userId =>
        path("friends") {
          get { ctx =>
            objectActor ! GetFriendsList(ctx, new Identifier(userId))
          }
        } ~
        path("add_friend" / FBID) { friendId =>
          post { ctx =>
            objectActor ! AddFriend(ctx, new Identifier(userId), new Identifier(friendId))
          }
        } ~
        path("post" / FBID) { friendId =>
          post {
            entity(as[PostCreateForm]) { form => ctx =>
              objectActor ! CreatePost(ctx, new Identifier(userId), new Identifier(friendId), form)
            }
          }
        } ~
        get { ctx =>
          objectActor ! GetUser(ctx, new Identifier(userId))
        }
      } ~
      pathEndOrSingleSlash {
        post {
          // Receive a JSON entity that acts as a form
          entity(as[UserCreateForm]) { user => ctx =>
            objectActor ! CreateUser(ctx, user)
          }
        } ~
        get {
          // TODO: need a session object for this to work
          complete("Yourself")
        }
      }
    } ~
    pathPrefix("profile") {
      ObjectID { id =>
        get {
          complete {
            // TODO: perform redirect to the appropriate object: Page or User
            s"Profile $id"
          }
        }
      } ~
      get {
        complete {
          // TODO: redirect to user's profile (/user)
          // This requires a working session object

          // In our Facebook model, you must be logged in as a user account, so this always
          // returns a user
          "Get the logged in user's profile"
        }
      }
    } ~
      pathPrefix("post") {
        ObjectID { me => ObjectID { postId =>
          get { ctx =>
            objectActor ! GetPost(ctx, new Identifier(me), new Identifier(postId))
          }
        } }
      } ~
    pathPrefix("page") {
      ObjectID { pageId =>
        get { ctx =>
          objectActor ! GetPage(ctx, new Identifier(pageId))
        }
      } ~
      post {
        // Receive a JSON entity that acts as a form
        entity(as[PageCreateForm]) { form => ctx =>
          objectActor ! CreatePage(ctx, form)
        }
      }
    } ~
    pathPrefix("album") {
      ObjectID { albumId =>
        post {
          entity(as[AlbumCreateForm]) { form => ctx =>
            objectActor ! CreateAlbum(ctx, new Identifier(albumId), form)
          }
        } ~
        get { ctx =>
          objectActor ! GetAlbum(ctx, new Identifier(albumId))
        }
      }
    } ~
    pathPrefix("picture") {
      ObjectID { pictureId =>
        post {
          entity(as[PictureCreateForm]) { form => ctx =>
            objectActor ! CreatePicture(ctx, new Identifier(pictureId), form)
          }
        } ~
          get { ctx =>
            objectActor ! GetPicture(ctx, new Identifier(pictureId))
          }
      }
    } ~
    path("image") {
      post {
        entity(as[MultipartFormData]) {
          formData => {
            val ftmp = File.createTempFile("upload", ".tmp", new File("tmp"))
            val output = new FileOutputStream(ftmp)
            formData.fields.foreach(f => output.write(f.entity.data.toByteArray ) )
            output.close()
            complete("done, file in: " + ftmp.getName())
          }
        }
      }
    }
}
