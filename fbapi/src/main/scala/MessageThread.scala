import spray.routing.PathMatchers._

case class MessageThread()

// A custom Facebook ID specifically for messages
object MessageThreadID extends NumberMatcher[Int](Int.MaxValue, 10) {
  def fromChar(c: Char) = fromDecimalChar(c)
}


