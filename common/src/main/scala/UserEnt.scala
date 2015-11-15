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