import java.util.TimeZone
//import spray.http.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._
import com.github.nscala_time.time.Imports._

object FacebookJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object IdentifierJsonFormat extends JsonFormat[Identifier] {
    def write(id: Identifier) = JsString("0")

    def read(value: JsValue) = value match {
      case JsString(a) => new Identifier(a.toInt)
      case _ => deserializationError("ID expected")
    }
  }

  implicit object DateTimeJsonFormat extends JsonFormat[DateTime] {
    private val dateTimeFmt = org.joda.time.format.ISODateTimeFormat.dateTime

    def write(datetime: DateTime) = JsString(datetime.toString())

    def read(value: JsValue) = value match {
      case JsString(a) =>
        dateTimeFmt.parseDateTime(a)
        //val result = DateTime.from
        //val result = DateTime.fromIsoDateTimeString(a.toString)

        /*if(result.nonEmpty)
          result.get
        else
          deserializationError("Invalid DateTime ISO")*/
      case _ => deserializationError("DateTime string expected")
    }
  }

  implicit object GenderJsonFormat extends JsonFormat[Gender.EnumVal] {
    def write(gender: Gender.EnumVal) = JsString(gender.toString)

    def read(value: JsValue) = value match {
      case JsString(a) => Gender withName a
      case _ => deserializationError("Gender string expected")
    }
  }

  implicit object RelationshipJsonFormat extends JsonFormat[RelationshipStatus.EnumVal] {
    def write(status: RelationshipStatus.EnumVal) = JsString(status.toString)

    def read(value: JsValue) = value match {
      case JsString(a) => RelationshipStatus withName a
      case _ => deserializationError("RelationshipStatus string expected")
    }
  }

  implicit object PoliticalJsonFormat extends JsonFormat[PoliticalAffiliation.EnumVal] {
    def write(affil: PoliticalAffiliation.EnumVal) = JsString(affil.toString)

    def read(value: JsValue) = value match {
      case JsString(a) => PoliticalAffiliation withName a
      case _ => deserializationError("PoliticalAffiliation string expected")
    }
  }

  implicit object TimeZoneJsonFormat extends JsonFormat[TimeZone] {
    def write(tz: TimeZone) = JsString(tz.getID)

    def read(value: JsValue) = value match {
      case JsString(a) => TimeZone.getTimeZone(a)
      case _ => deserializationError("TimeZone string expected")
    }
  }

  implicit val userEntFormat  = jsonFormat12(UserEnt)
  implicit val userCreateFormFormat  = jsonFormat10(UserCreateForm)
  implicit val pageEntFormat = jsonFormat8(PageEnt)
}