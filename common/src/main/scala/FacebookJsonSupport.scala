import java.util.TimeZone
import org.joda.time.DateTime
import spray.httpx.SprayJsonSupport
import spray.json._

object FacebookJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object IdentifierJsonFormat extends JsonFormat[Identifier] {
    def write(id: Identifier) = JsString(id.toString)

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

  ///////////////////////////////////////////////////

  /**
    * Adds FacebookEntity metadata to a JsObject
    * @param obj
    * @param ent
    * @return JsObject
    */
  def annotate(obj : JsObject, ent : FacebookEntity) = {
    var objFields = obj.fields
    objFields += (
      "id" -> JsString(ent.id.toString),
      "modified_time" -> JsString(ent.modified_time.toString)
      )
    JsObject(objFields)
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
        "status" -> JsString(ent.status),
        "public_key" -> JsString(ent.public_key)
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "first_name", "last_name", "birthday",
        "gender", "email", "about", "relationship_status",
        "interested_in", "political", "tz", "status", "public_key") match {
        case Seq(id, modified_time, JsString(first_name), JsString(last_name), birthday,
        gender, JsString(email), JsString(about), relationship_status,
        interested_in, political, tz, JsString(status), JsString(public_key)
        ) =>
          val ent = new UserEnt(id.convertTo[Identifier], first_name, last_name, birthday.convertTo[DateTime],
            gender.convertTo[Gender.EnumVal], email, about, relationship_status.convertTo[RelationshipStatus.EnumVal],
            interested_in.convertTo[Gender.EnumVal], political.convertTo[PoliticalAffiliation.EnumVal], tz.convertTo[TimeZone],
            status, public_key
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

  implicit object PostEntJsonFormat extends RootJsonFormat[PostEnt] {
    def write(ent : PostEnt) = {
      annotate(JsObject(
        "owner" -> JsString(ent.owner.toString),
        "target" -> JsString(ent.target.toString),
        "content" -> ent.content.toJson,
        "key" -> ent.key.toJson,
        "nonce" -> ent.nonce.toJson
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "owner", "target", "content", "key", "nonce") match {
        case Seq(id, modified_time, owner, target, JsString(content), JsString(key), JsString(nonce)) =>
          val ent = new PostEnt(id.convertTo[Identifier], owner.convertTo[Identifier],
            target.convertTo[Identifier], content, key, nonce
          )
          ent.modified_time = modified_time.convertTo[DateTime]
          ent
        case unk : Any => deserializationError("Invalid PostEnt format: " + unk.toString)
      }
    }
  }

  implicit object AlbumEntJsonFormat extends RootJsonFormat[AlbumEnt] {
    def write(ent : AlbumEnt) = {
      annotate(JsObject(
        "owner" -> JsString(ent.owner.toString),
        "name" -> ent.name.toJson,
        "description" -> ent.description.toJson
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "owner", "name", "description") match {
        case Seq(id, modified_time, owner, JsString(name), JsString(content)) =>
          val ent = new AlbumEnt(id.convertTo[Identifier], owner.convertTo[Identifier],
            name, content
          )
          ent.modified_time = modified_time.convertTo[DateTime]
          ent
        case unk : Any => deserializationError("Invalid AlbumEnt format: " + unk.toString)
      }
    }
  }

  implicit object PictureEntJsonFormat extends RootJsonFormat[PictureEnt] {
    def write(ent : PictureEnt) = {
      annotate(JsObject(
        "album_id" -> JsString(ent.albumId.toString),
        "caption" -> ent.caption.toJson,
        "file_id" -> JsString(ent.fileId.toString)
      ), ent)
    }

    def read(value : JsValue) = {
      value.asJsObject.getFields("id", "modified_time", "album_id", "caption", "file_id") match {
        case Seq(id, modified_time, owner, JsString(caption), fileId) =>
          val ent = new PictureEnt(id.convertTo[Identifier], owner.convertTo[Identifier],
            caption, fileId.convertTo[Identifier]
          )
          ent.modified_time = modified_time.convertTo[DateTime]
          ent
        case unk : Any => deserializationError("Invalid PictureEnt format: " + unk.toString)
      }
    }
  }

  // Forms JSON formats
  implicit val userFormFormat  = jsonFormat11(UserCreateForm)
  implicit val pageEntFormFormat = jsonFormat7(PageCreateForm)
  implicit val postEntFormFormat = jsonFormat3(PostCreateForm)
  implicit val albumEntFormFormat = jsonFormat2(AlbumCreateForm)
  implicit val pictureFormFormat = jsonFormat2(PictureCreateForm)
}