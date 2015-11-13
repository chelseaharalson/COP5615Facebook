import java.util.TimeZone
import spray.http.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._

case class UserEnt(id : Identifier,
                   first_name : String,
                   last_name : String,
                   birthday : DateTime,
                   gender : Gender.EnumVal,
                   email : String,
                   about : String,
                   relationship_status : RelationshipStatus.EnumVal,
                   interested_in : Gender.EnumVal,
                   political : PoliticalAffiliation.EnumVal,
                   last_updated : DateTime,
                   tz : TimeZone,
                   status : String) {

  // default constructor for creating an empty person
  def this() = this(new Identifier(0), "", "", DateTime.now, Gender.Unspecified, "", "",
                    RelationshipStatus.Complicated, Gender.Unspecified, PoliticalAffiliation.Independent,
                    DateTime.now, TimeZone.getDefault, "")
}

object PersonJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object IdentifierJsonFormat extends JsonFormat[Identifier] {
    def write(id: Identifier) = JsString("0")

    def read(value: JsValue) = value match {
      case JsString(a) => new Identifier(a.toInt)
      case _ => deserializationError("ID expected")
    }
  }

  implicit object DateTimeJsonFormat extends JsonFormat[DateTime] {
    def write(datetime: DateTime) = JsString(datetime.toString())

    def read(value: JsValue) = value match {
      case JsString(a) =>
        val result = DateTime.fromIsoDateTimeString(a.toString)

        if(result.nonEmpty)
          result.get
        else
          deserializationError("Invalid DateTime ISO")
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

  implicit val personFormat  = jsonFormat13(UserEnt)
}