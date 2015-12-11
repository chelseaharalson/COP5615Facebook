import java.util.TimeZone
import com.github.nscala_time.time.Imports._

case class UserCreateForm(first_name : String,
                   last_name : String,
                   birthday : DateTime,
                   gender : Gender.EnumVal,
                   email : String,
                   about : String,
                   relationship_status : RelationshipStatus.EnumVal,
                   interested_in : Gender.EnumVal,
                   political : PoliticalAffiliation.EnumVal,
                   tz : TimeZone,
                   public_key : String)

case class addFriend(requester : Identifier, target : Identifier)