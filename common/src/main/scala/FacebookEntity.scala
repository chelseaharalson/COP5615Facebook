import spray.http.DateTime

class FacebookEntity(id : Identifier) {
  private var modified_time : DateTime = DateTime.now

  def modifiedTime = modified_time
  def touch() = modified_time = DateTime.now
}
