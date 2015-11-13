import java.util.TimeZone
import scala.util.Random
import akka.actor._
import com.github.nscala_time.time.Imports._
import org.joda.time.Days

class UserActor(pFirstName: String, pLastName: String, pGender: String) extends Actor {
  var firstName: String = ""
  var lastName: String = ""
  var birthday: DateTime = DateTime.now
  var gender: String = ""
  var email: String = ""
  var about: String = ""
  var relationshipStatus: String = ""
  var status: String = ""
  var interestedIn: String = ""
  var political: String = ""
  var last_updated: DateTime = DateTime.now
  //var tz: TimeZone

  firstName = pFirstName
  lastName = pLastName
  gender = pGender
  birthday = generateBirthday
  email = firstName + "." + lastName + "@gmail.com"
  relationshipStatus = generateRelationshipStatus
  political = generatePoliticalStatus
  interestedIn = generateInterestedIn(gender)
  about = generateAbout(gender, interestedIn, relationshipStatus, political)

  println(firstName + " " + lastName + " " + gender + " " + about)

  def receive = {
    case CreateUser => {
      println("Hi from create user")
    }
  }

  def generateBirthday: DateTime = {
    /*0% 		13 - 17
    15%		18 - 24
    29%		25 - 34
    24%		35 - 44
    21%		45 - 54
    9%		55+*/

    val p: Integer = Random.nextInt(100)
    var bd: DateTime = DateTime.now

    if (p < 15) {
      bd = bd.plusYears(-18).plusDays(-Random.nextInt(365*6))
    }
    else if (p < 44) {
      bd = bd.plusYears(-25).plusDays(-Random.nextInt(365*9))
    }
    else if (p < 68) {
      bd = bd.plusYears(-35).plusDays(-Random.nextInt(365*9))
    }
    else if (p < 89) {
      bd = bd.plusYears(-45).plusDays(-Random.nextInt(365*9))
    }
    else {
      bd = bd.plusYears(-55).plusDays(-Random.nextInt(365*15))
    }
    bd
  }

  def generateRelationshipStatus: String = {
    /*
    * Single	37 %
      Married	31 %
      In a Relationship	24 %
      Engaged	3 %
      It’s Complicated	3 %
    * */
    val p: Integer = Random.nextInt(100)
    var relStatus: String = ""
    if (p < 37) {
      relStatus = "S"
    }
    else if (p < 68) {
      relStatus = "M"
    }
    else if (p < 92) {
      relStatus = "R"
    }
    else if (p < 95) {
      relStatus = "E"
    }
    else {
      relStatus = "C"
    }
    relStatus
  }

  def generatePoliticalStatus: String = {
    /*
    Republicans 25%
    Independents 42%
    Democrats 29%
    */
    val p: Integer = Random.nextInt(100)
    var polStatus: String = ""
    if (p < 25) {
      polStatus = "R"
    }
    else if (p < 54) {
      polStatus = "D"
    }
    else {
      polStatus = "I"
    }
    polStatus
  }

  def generateInterestedIn(pGender: String): String = {
    val p: Integer = Random.nextInt(1000)
    var interestedIn: String = ""
    if (pGender == "M") {
      interestedIn = "F"
    }
    else {
      interestedIn = "M"
    }
    if (p < 7) {
      interestedIn = "B"
    }
    else if (p < 24) {
      interestedIn = pGender
    }
    interestedIn
  }

  def generateAbout(pGender: String, pInterestedIn: String, pRelStatus: String, pPolStatus: String): String = {
    var about: String = ""
    var gender: String = ""
    var relStat: String = ""
    var intIn: String = ""
    var polStat: String = ""
    if (pGender == "F") gender = "woman"
    if (pGender == "M") gender = "man"
    if (pInterestedIn == "F") intIn = "women"
    if (pInterestedIn == "M") intIn = "men"
    if (pInterestedIn == "B") intIn = "women/men"
    if (pRelStatus == "S") relStat = "single"
    if (pRelStatus == "M") relStat = "married"
    if (pRelStatus == "R") relStat = "in a relationship"
    if (pRelStatus == "E") relStat = "engaged"
    if (pRelStatus == "C") relStat = "it's complicated"
    if (pPolStatus == "R") polStat = "Republican"
    if (pPolStatus == "D") polStat = "Democrat"
    if (pPolStatus == "I") polStat = "Independent"

    about = "I am a " + gender + " that is interested in " + intIn + ". I am currently " + relStat + " and my political " +
      "affliation is " + polStat + "."
    about
  }

}
