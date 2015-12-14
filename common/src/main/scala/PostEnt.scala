class PostEnt(id : Identifier,
              var owner : Identifier,
              var target : Identifier,
              var content : String,
              var key : String,
              var nonce : String,
              var digitalSig : String) extends FacebookEntity(id) {

  def this() = this(new Identifier(0), new Identifier(0),
    new Identifier(0), "", "", "", "")
}
