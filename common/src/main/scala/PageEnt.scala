class PageEnt(id : Identifier,
                   var name : String,
                   var about : String,
                   var business : String,
                   var contact_address : String,
                   var description : String,
                   var location : String,
                   var phone_number : String) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), "", "", "", "", "", "", "")
}
