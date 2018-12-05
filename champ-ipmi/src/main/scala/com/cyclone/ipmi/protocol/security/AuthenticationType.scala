package com.cyclone.ipmi.protocol.security

import java.security.MessageDigest

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.Password
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet.SessionSequenceNumber

trait AuthenticationType {
  val code: Byte

  def generateAuthCode(
    password: Password,
    sessionId: ManagedSystemSessionId,
    sequenceNumber: SessionSequenceNumber,
    payload: ByteString): ByteString
}

object AuthenticationType {

  private def digestBase(
    password: Password,
    sessionId: ManagedSystemSessionId,
    sequenceNumber: SessionSequenceNumber,
    payload: ByteString) = {
    val b = new ByteStringBuilder

    b ++= password.toBin
    b ++= sessionId.toBin
    b ++= payload
    b ++= sequenceNumber.toBin
    b ++= password.toBin

    b.result()
  }

  protected trait MessageDigestAuthenticationType extends AuthenticationType {
    def algorithmName: String

    lazy val md: MessageDigest = MessageDigest.getInstance(algorithmName)

    def generateAuthCode(password: Password, sessionId: ManagedSystemSessionId,
      sequenceNumber: SessionSequenceNumber, payload: ByteString) =
      ByteString(md.digest(digestBase(password, sessionId, sequenceNumber, payload).toArray))
  }

  implicit val codec: Codec[AuthenticationType] = new Codec[AuthenticationType] {
    def encode(a: AuthenticationType) =
      ByteString(a.code)

    def decode(data: ByteString): AuthenticationType = {
      val code = data(0)

      // We should have negotiated available algorithms:
      // if it is not available something unexpected has gone wrong
      fromCode(code)
        .getOrElse(throw new IllegalArgumentException(s"Unsupported authentication type $code"))
    }
  }

  case object NoAuth extends AuthenticationType {
    val code: Byte = 0.toByte

    def generateAuthCode(password: Password, sessionId: ManagedSystemSessionId,
      sequenceNumber: SessionSequenceNumber, payload: ByteString): ByteString =
      ByteString.empty
  }

  case object MD2 extends MessageDigestAuthenticationType {
    val code: Byte = 1.toByte

    def algorithmName = "MD2"
  }

  case object MD5 extends MessageDigestAuthenticationType {
    val code: Byte = 2.toByte

    def algorithmName = "MD5"
  }

  case object UsePassword extends AuthenticationType {
    val code: Byte = 4.toByte

    def generateAuthCode(password: Password, sessionId: ManagedSystemSessionId,
      sequenceNumber: SessionSequenceNumber, payload: ByteString): ByteString =
      password.toBin
  }

  case object RmcpPlus extends AuthenticationType {
    val code: Byte = 6.toByte

    def generateAuthCode(password: Password, sessionId: ManagedSystemSessionId,
      sequenceNumber: SessionSequenceNumber, payload: ByteString) =
      throw new IllegalStateException("N/A for RMCP+")
  }

  def fromCode(code: Byte): Option[AuthenticationType] = code match {
    case NoAuth.code      => Some(NoAuth)
    case MD2.code         => Some(MD2)
    case MD5.code         => Some(MD5)
    case UsePassword.code => Some(UsePassword)
    case RmcpPlus.code    => Some(RmcpPlus)
    case _                => None
  }

  private val supportedTypesMostSecureFirst: List[AuthenticationType] =
    List(MD5, MD2, UsePassword, NoAuth)

  def mostSecureOf(proposedTypes: Set[AuthenticationType]): Option[AuthenticationType] =
    supportedTypesMostSecureFirst.find(proposedTypes.contains)
}

case class AuthenticationTypes(types: Set[AuthenticationType])

object AuthenticationTypes {
  implicit val decoder: Decoder[AuthenticationTypes] = new Decoder[AuthenticationTypes] {
    def decode(data: ByteString): AuthenticationTypes =
      fromBits(data(0).bits0To5)
  }

  private val masks = Set(
    0x01 -> AuthenticationType.NoAuth,
    0x02 -> AuthenticationType.MD2,
    0x04 -> AuthenticationType.MD5,
    0x10 -> AuthenticationType.UsePassword
  )

  def fromBits(bits: Byte): AuthenticationTypes =
    AuthenticationTypes(masks.collect {
      case (mask, authType) if (bits & mask) != 0 => authType
    })

}
