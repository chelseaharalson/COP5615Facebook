
object Gender {
  sealed trait EnumVal
  case object Male extends EnumVal
  case object Female extends EnumVal
  case object Unspecified extends EnumVal
}