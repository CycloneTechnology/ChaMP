package com.cyclone.ipmi.protocol.packet

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.IntegrityCheckError
import com.cyclone.ipmi.codec.{Coder, _}
import com.cyclone.ipmi.protocol.SessionContext
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.security.AuthenticationType
import scalaz.Scalaz._
import scalaz.\/

/**
  * Classes representing an IPMI v1.5 request and response messages
  */
object Ipmi15SessionWrapper {

  object Request {
    implicit def coder: Coder[Request] = new Coder[Request] {

      def encode(message: Request): ByteString = {
        import message._

        val b = new ByteStringBuilder

        b ++= authenticationType.toBin

        b ++= sessionSequenceNumber.toBin
        b ++= managedSystemSessionId.toBin

        val payloadBytes = payload.encode
        b ++= requestHashMaker(managedSystemSessionId, sessionSequenceNumber, payloadBytes)

        val payLoadBytes = payload.encode
        b += payLoadBytes.length.toByte
        b ++= payLoadBytes

        // PAD
        b += 0

        b.result()
      }
    }

    def fromContext[P <: IpmiRequestPayload: Coder](
      payload: P,
      sessionSequenceNumber: SessionSequenceNumber,
      sessionContext: SessionContext
    ): Request = {
      val coder = implicitly[Coder[P]]

      Request(
        authenticationType = sessionContext.authenticationType,
        managedSystemSessionId = sessionContext.managedSystemSessionId,
        sessionSequenceNumber = sessionSequenceNumber,
        requestHashMaker = sessionContext.requestHashMaker,
        payload = Codable(payload)
      )
    }
  }

  case class Request(
    authenticationType: AuthenticationType,
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    requestHashMaker: SessionContext.ReqHashMaker,
    payload: Codable
  ) extends IpmiSessionWrapperRequest {
    val payloadType: PayloadType = PayloadType.Ipmi
  }

  object RawResponse {
    implicit def decoder: Decoder[RawResponse] = new Decoder[RawResponse] {

      def decode(data: ByteString): RawResponse = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val authType = is.readByte.as[AuthenticationType]

        val sessSeqNo = is.read(4).as[SessionSequenceNumber]
        val sessionId = is.read(4).as[ManagedSystemSessionId]

        val authCode =
          if (authType != AuthenticationType.NoAuth)
            is.read(16)
          else
            ByteString.empty

        val payloadLen = is.readByte.toUnsignedInt

        val payload = is.read(payloadLen)

        RawResponse(
          authenticationType = authType,
          managedSystemSessionId = sessionId,
          sessionSequenceNumber = sessSeqNo,
          payload = payload,
          authCode = authCode
        )
      }
    }
  }

  case class RawResponse(
    authenticationType: AuthenticationType,
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    payload: ByteString,
    authCode: ByteString
  ) extends IpmiSessionWrapperRawResponse {
    val payloadType: PayloadType.Ipmi.type = PayloadType.Ipmi

    def authenticatedResponse(sessionContext: SessionContext): IntegrityCheckError \/ Response = {
      val optErrMsg = for {
        hash <- sessionContext.responseHashMaker(
          managedSystemSessionId,
          sessionSequenceNumber,
          payload
        )

        errorMsg <- (sessionContext.requireResponseAuthCheck(authenticationType) && hash != authCode)
          .option(s"Integrity check failed $hash != $authCode")
      } yield errorMsg

      optErrMsg match {
        case Some(msg) => IntegrityCheckError(msg).left
        case None =>
          Response(
            authenticationType = authenticationType,
            managedSystemSessionId = managedSystemSessionId,
            sessionSequenceNumber = sessionSequenceNumber,
            payload = payload
          ).right
      }
    }
  }

  case class Response(
    authenticationType: AuthenticationType,
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    payload: ByteString
  ) extends IpmiSessionWrapperResponse {
    val payloadType: PayloadType = PayloadType.Ipmi
  }

}
