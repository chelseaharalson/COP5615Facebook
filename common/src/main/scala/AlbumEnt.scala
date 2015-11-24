class AlbumEnt(id : Identifier,
              var owner : Identifier,
              var name : String,
              var description : String) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), new Identifier(0), "", "")
}
