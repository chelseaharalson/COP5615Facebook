import akka.actor._
import scala.util.Random

object FBClient {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("Facebook-System")
    val master = system.actorOf(Props(new Master()), "master")
    master ! CreateUsers

    /*for (i <- 0 until 5) {
      println("New: " + realRandom(10) + " Old: " + Random.nextInt(10))
    }

    for (i <- 0 until 5) {
      println("New: " + realRandom(10) + " Old: " + Random.nextInt(10))
    }*/
  }

  def realRandom(r : Int) : Int = {
    var i = Random.nextInt(1000)
    var f = i.toFloat / 1000
    var f1 = f * r
    f1.toInt
  }

}