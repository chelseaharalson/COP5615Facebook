import java.security._
import java.security.spec.{RSAPrivateKeySpec, RSAPublicKeySpec}
import javax.crypto.Cipher
import scala.collection.mutable

class RSAhelper {
  var keyMap = mutable.HashMap[PublicKey, PrivateKey]()

  def generateKeys() : mutable.HashMap[PublicKey, PrivateKey] = {
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048)
    val kp = kpg.genKeyPair()
    val publicKey = kp.getPublic()
    val privateKey = kp.getPrivate()

    keyMap += (publicKey -> privateKey)

    //println(keyMap)

    /*val fact = KeyFactory.getInstance("RSA")
    val pub = fact.getKeySpec(kp.getPublic(), classOf[RSAPublicKeySpec])
    val priv = fact.getKeySpec(kp.getPrivate(), classOf[RSAPrivateKeySpec])

    val pubMod = pub.getModulus()
    val pubExp = pub.getPublicExponent()
    val privMod = priv.getModulus()
    val privExp = priv.getPrivateExponent()*/

    //println(publicKey.toString + "\n" + privateKey.toString)
    //println()
    //println(pubMod + "\n" + pubExp + "\n" + privMod + "\n" + privExp)

    /*val pubKeySpec = new RSAPublicKeySpec(pubMod, pubExp)
    val pubFact = KeyFactory.getInstance("RSA")
    val pubKey = fact.generatePublic(pubKeySpec)

    val privKeySpec = new RSAPrivateKeySpec(privMod, privExp)
    val privFact = KeyFactory.getInstance("RSA")
    val privKey = fact.generatePrivate(privKeySpec)

    println(pubKey.toString + "   " + privKey.toString)*/
    keyMap
  }

  def encrypt(text : String, key : PublicKey) : Array[Byte] = {
    var cipherText = Array[Byte]()
    try {
      val cipher = Cipher.getInstance("RSA")
      cipher.init(Cipher.ENCRYPT_MODE, key)
      cipherText = cipher.doFinal(text.getBytes())
    } catch {
      case e: Exception => println(e)
    }
    cipherText
  }

  def decrypt(text : Array[Byte], key : PrivateKey) : String = {
    var decryptedText = Array[Byte]()
    try {
      val cipher = Cipher.getInstance("RSA")
      cipher.init(Cipher.DECRYPT_MODE, key)
      decryptedText = cipher.doFinal(text)
    } catch {
      case e: Exception => println(e)
    }
    new String(decryptedText)
  }

}
