import javax.crypto._
import javax.crypto.spec.GCMParameterSpec
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Arrays

class AEShelper {

  val AES_KEY_SIZE = 128
  val GCM_NONCE_LENGTH = 12
  val GCM_TAG_LENGTH = 16

  def generateKey() : SecretKey = {
    val random = SecureRandom.getInstanceStrong()
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(AES_KEY_SIZE, random)
    val key = keyGen.generateKey()
    key
  }

  def encrypt(key : SecretKey, input : String) : Array[Byte] = {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE")
    val nonce = new Array[Byte](GCM_NONCE_LENGTH)
    val random = SecureRandom.getInstanceStrong()
    random.nextBytes(nonce)
    val spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.ENCRYPT_MODE, key, spec)
    val aad = "Whatever I like".getBytes()
    cipher.updateAAD(aad)
    val cipherText = cipher.doFinal(input.getBytes())
    //println(cipherText)
    cipherText
  }

  def decrypt(key : SecretKey, cipherText : Array[Byte]) : String = {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE")
    val nonce = new Array[Byte](GCM_NONCE_LENGTH)
    val spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.DECRYPT_MODE, key, spec)
    val plainText = cipher.doFinal(cipherText)
    new String(plainText)
  }
}
