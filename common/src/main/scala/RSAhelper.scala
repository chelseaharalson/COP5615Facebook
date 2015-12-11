import java.security._
import java.security.spec.{X509EncodedKeySpec, RSAPrivateKeySpec, RSAPublicKeySpec}
import javax.crypto.Cipher
import java.util.Base64
import scala.collection.mutable

class RSAhelper {

  def generateKeys() : (PublicKey, PrivateKey) = {
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048, new SecureRandom())
    val kp = kpg.generateKeyPair()
    val publicKey = kp.getPublic()
    val privateKey = kp.getPrivate()

    (publicKey, privateKey)
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

  def convertToBase64(text : Array[Byte]) : String = {
    val s = Base64.getEncoder.encodeToString(text)
    s
  }

  def convertPublicKeyStr(public_key : PublicKey) : String = {
    val pub = Base64.getEncoder.encodeToString(public_key.getEncoded())
    pub
  }

  def convertPrivateKeyStr(private_key : PrivateKey) : String = {
    val priv = Base64.getEncoder.encodeToString(private_key.getEncoded())
    priv
  }

  def getPublicKey(public_key : String) : PublicKey = {
    val publicBytes = Base64.getDecoder.decode(public_key)
    val keySpec = new X509EncodedKeySpec(publicBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    val pubKey = keyFactory.generatePublic(keySpec)
    pubKey
  }

  def generateSignature(private_key : PrivateKey, text : String) : Array[Byte] = {
    val signature = Signature.getInstance("SHA1withRSA")
    signature.initSign(private_key, new SecureRandom())
    val data = text.getBytes()
    signature.update(data)
    val sigBytes = signature.sign()
    //println("Signature: " + sigBytes.toString)
    sigBytes
  }

  def verifySignature(public_key : PublicKey, sigBytes : Array[Byte], text : String) : Boolean = {
    val signature = Signature.getInstance("SHA1withRSA")
    signature.initVerify(public_key)
    val data = text.getBytes()
    signature.update(data)
    val verifies = signature.verify(sigBytes)
    //println("Signature verifies: " + verifies)
    verifies
  }

}
