import java.util.TimeZone

import spray.http.DateTime

case class UserCreateForm(first_name : String,
                   last_name : String,
                   birthday : DateTime,
                   gender : Gender.EnumVal,
                   email : String,
                   about : String,
                   relationship_status : RelationshipStatus.EnumVal,
                   interested_in : Gender.EnumVal,
                   political : PoliticalAffiliation.EnumVal,
                   tz : TimeZone)