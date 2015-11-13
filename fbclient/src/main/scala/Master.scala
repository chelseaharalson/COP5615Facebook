import akka.actor._

class Master extends Actor {

  var girlFirstNames = Array("Emma",
    "Olivia",
    "Sophia",
    "Isabella",
    "Ava",
    "Mia",
    "Emily",
    "Abigail",
    "Madison",
    "Charlotte")

  var boyFirstNames = Array("Noah",
    "Liam",
    "Mason",
    "Jacob",
    "William",
    "Ethan",
    "Michael",
    "Alexander",
    "James",
    "Daniel")

  var lastNames = Array("Smith",
    "Johnson",
    "Williams",
    "Jones",
    "Brown",
    "Davis",
    "Miller",
    "Wilson",
    "Moore",
    "Taylor")

  def receive = {
    case CreateUsers => {
      println("Hi from master")
      for (iFN <- 0 until girlFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          //println(girlFirstNames(iFN) + " " + lastNames(iLN))
          context.actorOf(Props(new UserActor(girlFirstNames(iFN), lastNames(iLN), "F")), girlFirstNames(iFN) + lastNames(iLN))
        }
      }

      for (iFN <- 0 until boyFirstNames.size) {
        for (iLN <- 0 until lastNames.size) {
          //println(girlFirstNames(iFN) + " " + lastNames(iLN))
          context.actorOf(Props(new UserActor(boyFirstNames(iFN), lastNames(iLN), "M")), boyFirstNames(iFN) + lastNames(iLN))
        }
      }
    }
  }

}
