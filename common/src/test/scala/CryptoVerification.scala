import java.util.Base64
import org.specs2.mutable.Specification

class CryptoVerification extends Specification {

  "Verifying a digital signature" should {
    val originalText = "Text to be encrypted "
    val rsa = new RSAhelper()
    val r = rsa.generateKeys()
    val pub = rsa.convertPublicKeyStr(r._1)
    val sig = rsa.generateSignature(r._2, originalText)

    "Return true" in {
      val verify = rsa.verifySignature(r._1, sig, originalText)
      if (verify == false) println("Return true: FAILED")
      ok
    }
    "Return false" in {
      val verify = rsa.verifySignature(r._1, sig, "This is corrupted text.")
      if (verify == true) println("Return false: FAILED")
      ok
    }
  }

  "Encrypting and decrypting successfully" should {
    val rsa = new RSAhelper()
    val r = rsa.generateKeys()
    val pub_key = rsa.convertPublicKeyStr(r._1)
    val priv_key = r._2
    val aes = new AEShelper()
    val msg = "This is the test message."

    "Success" in {
      val triple = aes.encryptMessage(msg, pub_key)
      val strRSA = aes.decryptMessage(triple._1, priv_key, triple._2, triple._3)
      println("Original Message: " + msg)
      println("Encrypted Message: " + triple._1)
      println("Decrypted Message: " + strRSA)
      ok
    }
  }

  "Verifying digital signature and encrypting/decrypting" should {
    val RSA = new RSAhelper()
    val rkeys = RSA.generateKeys()
    val pub_key = RSA.convertPublicKeyStr(rkeys._1)
    val private_key = rkeys._2

    val AES = new AEShelper()
    val post = "This is a test post."
    val triple = AES.encryptMessage(post, pub_key)

    // Generate digital signature
    val r = RSA.generateKeys()
    val public_key = RSA.convertPublicKeyStr(r._1)
    val digital_sig = RSA.generateSignature(r._2, triple._1)
    val str_sig = Base64.getEncoder.encodeToString(digital_sig)

    "Success" in {
      val decMsg = AES.decryptMessage(triple._1, private_key, triple._2, triple._3)
      val pub_key = RSA.getPublicKey(public_key)
      val sig = Base64.getDecoder.decode(str_sig)
      val verify = RSA.verifySignature(pub_key, sig, triple._1)
      if (verify == true) {
        println("**************** Decrypted Message: " + decMsg)
      }
      else {
        println("Failed to verify digital signature")
      }
      ok
    }
  }

}
