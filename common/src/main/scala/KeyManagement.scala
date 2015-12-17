case class KeyMaterial(key : String, nonce : String, sig : String)
case class KeyedEnt(entity : FacebookEntity, key : KeyMaterial)
