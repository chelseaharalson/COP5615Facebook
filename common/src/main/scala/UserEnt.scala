import java.util.TimeZone
import com.github.nscala_time.time.Imports._

// TODO: figure out how to include modified time with this case class
class UserEnt(id : Identifier,
             var first_name : String,
             var last_name : String,
             var birthday : DateTime,
             var gender : Gender.EnumVal,
             var email : String,
             var about : String,
             var relationship_status : RelationshipStatus.EnumVal,
             var interested_in : Gender.EnumVal,
             var political : PoliticalAffiliation.EnumVal,
             var tz : TimeZone,
             var status : String,
             var public_key : String) extends FacebookEntity(id) {

  // default constructor for creating an empty person
  def this() = this(new Identifier(0), "", "", DateTime.now(), Gender.Unspecified, "", "",
                    RelationshipStatus.Complicated, Gender.Unspecified, PoliticalAffiliation.Independent,
                    TimeZone.getDefault, "", "")
}