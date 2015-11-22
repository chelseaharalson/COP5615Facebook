import com.github.nscala_time.time.Imports._

class FacebookEntity(var id : Identifier) {
  var modified_time : DateTime = DateTime.now

  /**
    * Updates the object's modified time to reflect a change
    */
  def touch() = modified_time = DateTime.now
}
