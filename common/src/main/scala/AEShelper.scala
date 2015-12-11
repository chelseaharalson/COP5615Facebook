import javax.crypto._
import javax.crypto.spec.GCMParameterSpec
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Arrays

class AEShelper {

  val AES_KEY_SIZE = 128
  val GCM_NONCE_LENGTH = 12
  val GCM_TAG_LENGTH = 16

  val cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE")
  val random = SecureRandom.getInstanceStrong()

  def generateKey() : (SecretKey, Array[Byte]) = {
    val keyGen = KeyGenerator.getInstance("AES")
    val nonce = new Array[Byte](GCM_NONCE_LENGTH)
    keyGen.init(AES_KEY_SIZE, random)
    val key = keyGen.generateKey()
    random.nextBytes(nonce)
    (key, nonce)
  }

  def encrypt(key : SecretKey, nonce : Array[Byte], input : String) : Array[Byte] = {
    val spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.ENCRYPT_MODE, key, spec)
    val cipherText = cipher.doFinal(input.getBytes())
    //println("CIPHER TEXT: " + cipherText)
    cipherText
  }

  def decrypt(key : SecretKey, nonce : Array[Byte], cipherText : Array[Byte]) : String = {
    val spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.DECRYPT_MODE, key, spec)
    val plainText = cipher.doFinal(cipherText)
    //println("PLAIN TEXT: " + plainText)
    new String(plainText)
  }
}