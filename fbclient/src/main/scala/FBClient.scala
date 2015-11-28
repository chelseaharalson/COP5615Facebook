import java.io.File
//import akka.actor._
import akka.actor.ActorSystem
import spray.client.pipelining._
import spray.http.{MediaTypes, BodyPart, MultipartFormData}



object FBClient {
  def main(args: Array[String]): Unit = {
    //implicit val system = ActorSystem("Facebook-System")
    /*val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers
    val pg = system.actorOf(Props(new Page()), "page")
    pg ! CreatePages*/
    UploadFileExample
  }

  def UploadFileExample = {
    implicit val system = ActorSystem("simple-spray-client")
    import system.dispatcher // execution context for futures below

    val pipeline = sendReceive
    val payload = MultipartFormData(Seq(BodyPart(new File("Pictures/icon.png"), "datafile", MediaTypes.`application/base64`)))
    val request =
      Post("http://localhost:8080/user/image", payload)

    pipeline(request).onComplete { res =>
      println(res)
      system.shutdown()
    }
  }
}