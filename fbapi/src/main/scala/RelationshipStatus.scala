
object RelationshipStatus {
  sealed trait EnumVal
  case object Single extends EnumVal
  case object Complicated extends EnumVal
  case object Relationship extends EnumVal
}