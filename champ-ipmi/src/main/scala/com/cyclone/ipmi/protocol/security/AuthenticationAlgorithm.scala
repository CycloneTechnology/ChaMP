package com.cyclone.ipmi.protocol.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

/**
  * Algorithm for authentication during session activation.
  */
sealed trait AuthenticationAlgorithm {
  val code: Byte

  val goodness: Int

  def determineAuthCode(uid: Key.UID, data: ByteString): ByteString =
    doDetermineAuthCode(uid, data)

  def determineSik(kg: Key.KG, sikBase: ByteString): Key.SIK =
    Key.SIK(doDetermineAuthCode(kg, sikBase))

  def determineRakp4AuthCode(sik: Key.SIK, data: ByteString): ByteString =
    doDetermineAuthCode(sik, data)

  protected def doDetermineAuthCode(key: Key, data: ByteString): ByteString
}

object AuthenticationAlgorithm {
  implicit val ordering: Ordering[AuthenticationAlgorithm] = Ordering.by { alg: AuthenticationAlgorithm =>
    alg.goodness
  }

  implicit val codec: Codec[AuthenticationAlgorithm] = new Codec[AuthenticationAlgorithm] {

    def encode(a: AuthenticationAlgorithm) =
      ByteString(a.code)

    def decode(data: ByteString): AuthenticationAlgorithm = {
      val code = data(0)

      // We should have negotiated available algorithms:
      // if it is not available something unexpected has gone wrong
      fromCode(code)
        .getOrElse(
          throw new IllegalArgumentException(s"Unsupported authentication algorithm $code")
        )
    }
  }

  case object NoAuth extends AuthenticationAlgorithm {
    val code: Byte = SecurityConstants.AA_RAKP_NONE

    val goodness: Int = 0

    protected def doDetermineAuthCode(key: Key, data: ByteString): ByteString = ByteString.empty
  }

  protected trait MACBasedAuthenticationAlgorithm extends AuthenticationAlgorithm {
    protected val macAlgorithmName: String

    def doDetermineAuthCode(key: Key, data: ByteString): ByteString = {
      val mac = Mac.getInstance(macAlgorithmName)

      mac.init(new SecretKeySpec(key.byteArray, macAlgorithmName))

      ByteString(mac.doFinal(data.toArray))
    }
  }

  case object RakpHmacSha1 extends MACBasedAuthenticationAlgorithm {
    val code: Byte = SecurityConstants.AA_RAKP_HMAC_SHA1

    val goodness: Int = 10

    protected val macAlgorithmName: String = "HmacSHA1"

    /*
      See sec 13.28.1 RAKP-HMAC-SHA1 Authentication Algorithm uses HMAC-SHA1-96 which is just a 96-bit
      (12-byte) truncation of the full 160-bit (20-byte) HMAC-SHA1.
     */
    override def determineRakp4AuthCode(sik: Key.SIK, data: ByteString): ByteString =
      doDetermineAuthCode(sik, data).take(12)
  }

  def fromCode(code: Byte): Option[AuthenticationAlgorithm] = code match {
    case NoAuth.code       => Some(NoAuth)
    case RakpHmacSha1.code => Some(RakpHmacSha1)
    case _                 => None
  }
}
