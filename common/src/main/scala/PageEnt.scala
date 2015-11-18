case class PageEnt(id : Identifier,
                   name : String,
                   about : String,
                   business : String,
                   contact_address : String,
                   description : String,
                   location : String,
                   phone_number : String) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), "", "", "", "", "", "", "")
}
