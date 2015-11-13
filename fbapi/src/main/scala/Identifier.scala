import spray.routing.PathMatchers._

class Identifier(id : String) {
  def this(id : Int) = this(id.toString)

  override def toString() = id
}

// A custom Facebook ID type to allow for more complex IDs later
object FBID extends NumberMatcher[Int](Int.MaxValue, 10) {
  def fromChar(c: Char) = fromDecimalChar(c)
}