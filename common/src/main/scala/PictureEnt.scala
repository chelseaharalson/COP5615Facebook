class PictureEnt(id : Identifier,
               var albumId : Identifier,
               var caption : String,
               var fileId : Identifier) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), new Identifier(0), "", new Identifier(0))
}
