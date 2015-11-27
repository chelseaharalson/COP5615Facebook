import java.util.TimeZone
import org.joda.time.DateTime
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

    def write(datetime: DateTime) = JsString(datetime.toString)

    def read(value: JsValue) = value match {
      case JsString(a) =>
        dateTimeFmt.parseDateTime(a)
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

  def annotate(obj : JsObject, ent : FacebookEntity) = {
    var objFields = obj.fields
    objFields += (
      "id" -> JsString(ent.id.toString),
      "modified_time" -> JsString(ent.modified_time.toString)
      )
    JsObject(objFields)
  }

  implicit object FacebookEntityJsonFormat extends RootJsonFormat[FacebookEntity] {
    def write(ent : FacebookEntity) = ent match {
      case e : UserEnt =>
        var objFields = e.toJson.asJsObject.fields
        objFields += (
          "id" -> JsString(e.id.toString),
          "modified_time" -> JsString(e.modified_time.toString)
        )

        JsObject(objFields)
      case _ => serializationError("Unhandled FacebookEntity type")
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "object") match {
        case Seq(JsString(id), JsObject(ob)) =>
          new UserEnt()
        case _ => deserializationError("Invalid FacebookEntity")
      }
    }
  }

  implicit object UserEntJsonFormat extends RootJsonFormat[UserEnt] {
    def write(ent : UserEnt) = {
      annotate(JsObject(
        "first_name" -> JsString(ent.first_name),
        "last_name" -> JsString(ent.last_name),
        "birthday" -> ent.birthday.toJson,
        "gender" -> ent.gender.toJson,
        "email" -> JsString(ent.email),
        "about" -> JsString(ent.about),
        "relationship_status" -> ent.relationship_status.toJson,
        "interested_in" -> ent.interested_in.toJson,
        "political" -> ent.political.toJson,
        "tz" -> ent.tz.toJson,
        "status" -> JsString(ent.status)
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "first_name", "last_name", "birthday",
        "gender", "email", "about", "relationship_status",
        "interested_in", "political", "tz", "status") match {
        case Seq(id, modified_time, JsString(first_name), JsString(last_name), birthday,
        gender, JsString(email), JsString(about), relationship_status,
        interested_in, political, tz, JsString(status)
        ) =>
          val ent = new UserEnt(id.convertTo[Identifier], first_name, last_name, birthday.convertTo[DateTime],
            gender.convertTo[Gender.EnumVal], email, about, relationship_status.convertTo[RelationshipStatus.EnumVal],
            interested_in.convertTo[Gender.EnumVal], political.convertTo[PoliticalAffiliation.EnumVal], tz.convertTo[TimeZone],
            status
          )
          ent.modified_time = modified_time.convertTo[DateTime]
          ent
        case unk : Any => deserializationError("Invalid UserEnt format: " + unk.toString)
      }
    }
  }

  implicit object PageEntJsonFormat extends RootJsonFormat[PageEnt] {
    def write(ent : PageEnt) = {
      annotate(JsObject(
        "name" -> JsString(ent.name),
        "about" -> JsString(ent.about),
        "business" -> JsString(ent.business),
        "contact_address" -> JsString(ent.contact_address),
        "description" -> JsString(ent.description),
        "location" -> JsString(ent.location),
        "phone_number" -> JsString(ent.phone_number)
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "name", "about", "business",
        "contact_address", "description", "location", "phone_number") match {
        case Seq(id, modified_time, JsString(name), JsString(about), JsString(business),
        JsString(contact_address), JsString(description), JsString(location), JsString(phone_number)
        ) =>
          val ent = new PageEnt(id.convertTo[Identifier], name, about, business,
            contact_address, description, location, phone_number
          )
          ent.modified_time = modified_time.convertTo[DateTime]
          ent
        case unk : Any => deserializationError("Invalid PageEnt format: " + unk.toString)
      }
    }
  }

  implicit val userCreateFormFormat  = jsonFormat10(UserCreateForm)
  implicit val pageEntFormat = jsonFormat7(PageCreateForm)
}