
object PoliticalAffiliation {
  sealed trait EnumVal
  case object Democrat extends EnumVal
  case object Republican extends EnumVal
  case object Independent extends EnumVal
}