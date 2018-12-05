package com.cyclone.ipmi.protocol.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec.Codec

trait IntegrityAlgorithm {
  val code: Byte

  val goodness: Int

  protected val authCodeLength: Int

  def integrityPadding(base: ByteString): ByteString = {
    val lenWithAuthCode = base.length + 2 /* pad len + reserved byte */ + authCodeLength
    val pad = if (lenWithAuthCode % 4 != 0) 4 - lenWithAuthCode % 4 else 0

    val b = new ByteStringBuilder

    b ++= Array.fill(pad)(0xff.toByte)
    b += pad.toByte
    b += 0x07

    b.result()
  }

  def generateIntegrityCode(sik: Key.SIK, base: ByteString): ByteString
}

// Get these from the ipmi4j code - they are more comprehensive
object IntegrityAlgorithm {
  private val ConstKey1 = ByteString(Array.fill(20)(1.toByte))

  implicit val ordering: Ordering[IntegrityAlgorithm] = Ordering.by { alg: IntegrityAlgorithm => alg.goodness }

  implicit val codec: Codec[IntegrityAlgorithm] = new Codec[IntegrityAlgorithm] {
    def encode(a: IntegrityAlgorithm) =
      ByteString(a.code)

    def decode(data: ByteString): IntegrityAlgorithm = {
      val code = data(0)


      // We should have negotiated available algorithms:
      // if it is not available something unexpected has gone wrong
      fromCode(code)
        .getOrElse(throw new IllegalArgumentException(s"Unsupported integrity algorithm $code"))
    }
  }

  case object Open extends IntegrityAlgorithm {
    val code: Byte = SecurityConstants.IA_NONE

    val goodness: Int = 0

    protected val authCodeLength = 0

    def generateIntegrityCode(sik: Key.SIK, base: ByteString): ByteString = ByteString.empty
  }

  trait MACBasedIntegrityAlgorithm extends IntegrityAlgorithm {
    protected val macAlgorithmName: String

    def generateIntegrityCode(sik: Key.SIK, data: ByteString): ByteString = {

      val mac = initMac(sik)

      ByteString(mac.doFinal((data ++ integrityPadding(data)).toArray)).take(authCodeLength)
    }

    private def initMac(sik: Key.SIK): Mac = {
      // See 13.28.4
      val mac = Mac.getInstance(macAlgorithmName)

      val k1 = new SecretKeySpec(sik.byteArray, macAlgorithmName)
      mac.init(k1)

      val key2 = new SecretKeySpec(mac.doFinal(ConstKey1.toArray), macAlgorithmName)
      mac.init(key2)

      mac
    }
  }

  case object HmacSha1_96 extends MACBasedIntegrityAlgorithm {
    val code: Byte = SecurityConstants.IA_HMAC_SHA1_96

    val goodness: Int = 10

    protected val macAlgorithmName = "HmacSHA1"
    protected val authCodeLength = 12
  }


  def fromCode(code: Byte): Option[IntegrityAlgorithm] = code match {
    case Open.code        => Some(Open)
    case HmacSha1_96.code => Some(HmacSha1_96)
    case _                => None
  }


}