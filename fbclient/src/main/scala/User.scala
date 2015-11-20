import java.util.TimeZone
import scala.util.Random
import akka.actor._
import com.github.nscala_time.time.Imports._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

class User(pFirstName: String, pLastName: String, pGender: Gender.EnumVal) {
  var firstName: String = ""
  var lastName: String = ""
  var birthday: DateTime = DateTime.now
  var gender: Gender.EnumVal = Gender.Unspecified
  var email: String = ""
  var about: String = ""
  var relationshipStatus: RelationshipStatus.EnumVal = RelationshipStatus.Single
  var status: String = ""
  var interestedIn: Gender.EnumVal = Gender.Unspecified
  var political: PoliticalAffiliation.EnumVal = PoliticalAffiliation.Independent
  var last_updated: DateTime = DateTime.now
  var tz: TimeZone = TimeZone.getDefault

  val fileAbout = "TextFiles/About.txt"
  var abouts = parseFile(fileAbout)

  val fileStatus = "TextFiles/Status.txt"
  var statuses = parseFile(fileStatus)

  firstName = pFirstName
  lastName = pLastName
  gender = pGender
  birthday = generateBirthday
  email = firstName + "." + lastName + "@gmail.com"
  relationshipStatus = generateRelationshipStatus
  political = generatePoliticalStatus
  interestedIn = generateInterestedIn(gender)
  //about = generateAbout(gender, interestedIn, relationshipStatus, political)
  about = generateAbout(abouts)
  status = generateStatus(statuses)

  //println(firstName + " " + lastName + " " + gender + " " + about)

  //var s = generateCurlString(firstName, lastName, birthday, gender, email, about, relationshipStatus,
  //status, interestedIn, political, last_updated, tz)

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

  def generateRelationshipStatus: RelationshipStatus.EnumVal = {
    /*
    * Single	37 %
      Married	31 %
      In a Relationship	24 %
      Engaged	3 %
      It’s Complicated	3 %
    * */
    val p: Integer = Random.nextInt(100)
    var relStatus: RelationshipStatus.EnumVal = RelationshipStatus.Single
    if (p < 37) {
      relStatus = RelationshipStatus.Single
    }
    else if (p < 68) {
      relStatus = RelationshipStatus.Married
    }
    else if (p < 92) {
      relStatus = RelationshipStatus.Relationship
    }
    else if (p < 95) {
      relStatus = RelationshipStatus.Engaged
    }
    else {
      relStatus = RelationshipStatus.Complicated
    }
    relStatus
  }

  def generatePoliticalStatus: PoliticalAffiliation.EnumVal = {
    /*
    Republicans 25%
    Independents 42%
    Democrats 29%
    */
    val p: Integer = Random.nextInt(100)
    var polStatus: PoliticalAffiliation.EnumVal = PoliticalAffiliation.Independent
    if (p < 25) {
      polStatus = PoliticalAffiliation.Republican
    }
    else if (p < 54) {
      polStatus = PoliticalAffiliation.Democrat
    }
    else {
      polStatus = PoliticalAffiliation.Independent
    }
    polStatus
  }

  def generateInterestedIn(pGender: Gender.EnumVal): Gender.EnumVal = {
    val p: Integer = Random.nextInt(1000)
    var interestedIn: Gender.EnumVal = Gender.Unspecified
    if (pGender == Gender.Male) {
      interestedIn = Gender.Female
    }
    else {
      interestedIn = Gender.Male
    }
    if (p < 7) {
      interestedIn = Gender.Unspecified
    }
    else if (p < 24) {
      interestedIn = pGender
    }
    interestedIn
  }

  /*def generateAbout(pGender: String, pInterestedIn: String, pRelStatus: String, pPolStatus: String): String = {
    var about: String = ""
    var gender: String = ""
    var relStat: String = ""
    var intIn: String = ""
    if (pGender == "Female") gender = "woman"
    if (pGender == "Male") gender = "man"
    if (pInterestedIn == "Female") intIn = "women"
    if (pInterestedIn == "Male") intIn = "men"
    if (pInterestedIn == "Both") intIn = "women/men"
    if (pRelStatus == "Single") relStat = "single"
    if (pRelStatus == "Married") relStat = "married"
    if (pRelStatus == "Relationship") relStat = "in a relationship"
    if (pRelStatus == "Engaged") relStat = "engaged"
    if (pRelStatus == "Complicated") relStat = "it's complicated"

    about = "I am a " + gender + " that is interested in " + intIn + ". I am currently " + relStat + " and my political " +
      "affiliation is " + pPolStatus + "."
    about
  }*/

  def generateAbout(aboutArr: ArrayBuffer[String]) : String = {
    var about: String = ""
    val i: Integer = Random.nextInt(aboutArr.size)
    about = aboutArr(i)
    about
  }

  def generateStatus(statusArr: ArrayBuffer[String]) : String = {
    var status: String = ""
    val i: Integer = Random.nextInt(statusArr.size)
    status = statusArr(i)
    status
  }

  def generateCurlString(pFirstName: String, pLastName: String, pBirthday: DateTime,
    pGender: String, pEmail: String, pAbout: String, pRelationshipStatus: String,
    pStatus: String, pInterestedIn: String, pPolitical: String, pLastUpdated: DateTime, pTimeZone: TimeZone): String = {
    var str: String = ""
    str = "curl -H \"Content-Type: application/json\" -X POST -d '{ " +
      "\"first_name\": \"" + pFirstName + "\", \"last_name\": \"" + pLastName + "\", " +
      "\"birthday\": \"" + pBirthday.toString("yyyy-MM-dd'T'HH:mm:ss") + "\", \"gender\": \"" + pGender + "\", " +
      "\"email\": \"" + pEmail + "\", \"about\": \"" + pAbout + "\", " +
      "\"relationship_status\": \"" + pRelationshipStatus + "\", " +
      "\"interested_in\": \"" + pInterestedIn + "\", \"political\": \"" + pPolitical + "\", " +
      "\"tz\": \"" + pTimeZone.getID + "\", \"status\": \"" + pStatus +
      "\" }' http://localhost:8080/user/UserCreateFormTest"
    println(str)
    str
  }

  def parseFile(fileName: String): ArrayBuffer[String] = {
    var pfile = ArrayBuffer[String]()
    for (line <- Source.fromFile(fileName).getLines()) {
      pfile += line
    }
    pfile
  }

}
