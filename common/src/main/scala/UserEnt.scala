import java.util.TimeZone
import com.github.nscala_time.time.Imports._


// TODO: figure out how to include modified time with this case class
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
                   tz : TimeZone,
                   status : String) extends FacebookEntity(id) {

  // default constructor for creating an empty person
  def this() = this(new Identifier(0), "", "", DateTime.now, Gender.Unspecified, "", "",
                    RelationshipStatus.Complicated, Gender.Unspecified, PoliticalAffiliation.Independent,
                    TimeZone.getDefault, "")
}