class PostEnt(id : Identifier,
              var owner : Identifier,
              var target : Identifier,
              var content : String) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), new Identifier(0),
    new Identifier(0), "" )
}
