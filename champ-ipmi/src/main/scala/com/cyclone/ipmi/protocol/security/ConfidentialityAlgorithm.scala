package com.cyclone.ipmi.protocol.security

import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, Mac, SecretKey}

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec.Codec

sealed trait ConfidentialityAlgorithm {
  val code: Byte

  val goodness: Int

  def encrypt(sik: Key.SIK, data: ByteString): ByteString

  def decrypt(sik: Key.SIK, data: ByteString): ByteString
}

// Get these from the ipmi4j code - they are more comprehensive
object ConfidentialityAlgorithm {

  implicit val ordering: Ordering[ConfidentialityAlgorithm] = Ordering.by { alg: ConfidentialityAlgorithm =>
    alg.goodness
  }

  private val ConstKey2 = ByteString(Array.fill(20)(2.toByte))

  implicit val codec: Codec[ConfidentialityAlgorithm] = new Codec[ConfidentialityAlgorithm] {

    def encode(a: ConfidentialityAlgorithm) =
      ByteString(a.code)

    def decode(data: ByteString): ConfidentialityAlgorithm = {
      val code = data(0)

      // We should have negotiated available algorithms:
      // if it is not available something unexpected has gone wrong
      fromCode(code)
        .getOrElse(
          throw new IllegalArgumentException(s"Unsupported confidentiality algorithm $code")
        )
    }
  }

  case object Plain extends ConfidentialityAlgorithm {

    val code: Byte = SecurityConstants.CA_NONE

    val goodness: Int = 0

    def encrypt(sik: Key.SIK, data: ByteString): ByteString = data

    def decrypt(sik: Key.SIK, data: ByteString): ByteString = data
  }

  protected trait MACBasedConfidentialityAlgorithm extends ConfidentialityAlgorithm {
    protected val macAlgorithmName: String

    protected val keyAlgorithm: String

    protected val cipherAlgorithmName: String

    def encrypt(sik: Key.SIK, data: ByteString): ByteString = {
      val paddedData = {
        // Pad to nearest multiple of 16 after adding byte with pad length
        val pad = 16 - (data.length + 1 /* <-- pad len byte */ ) % 16

        val b = new ByteStringBuilder
        b ++= data
        b ++= (1 to pad).map(_.toByte)
        b += pad.toByte

        b.result()
      }

      val cipher = newCipherInstance
      val secretKey = initKey(sik)

      cipher.init(Cipher.ENCRYPT_MODE, secretKey)

      val b = new ByteStringBuilder
      b ++= cipher.getIV
      b ++= cipher.doFinal(paddedData.toArray)

      b.result()
    }

    def decrypt(sik: Key.SIK, data: ByteString): ByteString = {
      val (iv, encrypted) = data.splitAt(16)

      val cipher = newCipherInstance
      val secretKey = initKey(sik)

      cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.toArray))

      val decryptedPadded = ByteString(cipher.doFinal(encrypted.toArray))

      val pad = decryptedPadded.last

      decryptedPadded.take(decryptedPadded.length - pad - 1)
    }

    private def newCipherInstance: Cipher =
      Cipher.getInstance(cipherAlgorithmName)

    private def initKey(sik: Key.SIK): SecretKey = {
      val mac = Mac.getInstance(macAlgorithmName)

      val key1 = new SecretKeySpec(sik.byteArray, macAlgorithmName)
      mac.init(key1)

      val hash = ByteString(mac.doFinal(ConstKey2.toArray))
      new SecretKeySpec(hash.take(16).toArray, keyAlgorithm)
    }
  }

  case object AesCbc128 extends MACBasedConfidentialityAlgorithm {
    val code: Byte = SecurityConstants.CA_AES_CBC128

    val goodness: Int = 10

    protected val macAlgorithmName = "HmacSHA1"
    protected val keyAlgorithm = "AES"
    protected val cipherAlgorithmName = "AES/CBC/NoPadding"
  }

  def fromCode(code: Byte): Option[ConfidentialityAlgorithm] = code match {
    case Plain.code     => Some(Plain)
    case AesCbc128.code => Some(AesCbc128)
    case _              => None
  }
}
