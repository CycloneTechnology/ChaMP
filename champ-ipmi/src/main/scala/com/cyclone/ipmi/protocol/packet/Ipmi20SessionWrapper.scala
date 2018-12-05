package com.cyclone.ipmi.protocol.packet

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.IntegrityCheckError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.{Oem, SessionContext}
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.security.AuthenticationType
import scalaz.Scalaz._
import scalaz.\/

/**
  * Classes representing an IPMI v2.0 request and response messages
  */
object Ipmi20SessionWrapper {

  object Request {
    implicit def coder: Coder[Request] = new Coder[Request] {
      def encode(message: Request): ByteString = {
        import message._

        val b = new ByteStringBuilder

        b ++= authenticationType.toBin

        b += (payloadEncrypted.toBit7 | payloadAuthenticated.toBit6 | payloadType.code).toByte

        if (payloadType == PayloadType.Oem)
          oem.foreach { o =>
            b ++= o.toBin
          }

        b ++= managedSystemSessionId.toBin
        b ++= sessionSequenceNumber.toBin

        val encPayload = encryptor(payload)

        b ++= encPayload.length.toShort.toBin
        b ++= encPayload

        val baseMessage = b.result()

        val trailer = if (payloadAuthenticated && managedSystemSessionId.isSession)
          requestHashMaker(managedSystemSessionId, sessionSequenceNumber, baseMessage)
        else
          ByteString.empty

        baseMessage ++ trailer
      }
    }

    def fromContext[P <: IpmiRequestPayload : Coder](
      payload: P,
      sessionSequenceNumber: SessionSequenceNumber,
      sessionContext: SessionContext,
      oem: Option[Oem] = None): Request = {
      val coder = implicitly[Coder[P]]

      Request(
        managedSystemSessionId = sessionContext.managedSystemSessionId,
        sessionSequenceNumber = sessionSequenceNumber,
        payload = coder.encode(payload),
        payloadType = payload.payloadType,
        encryptor = sessionContext.encryptor,
        requestHashMaker = sessionContext.requestHashMaker,
        payloadEncrypted = sessionContext.payloadEncrypted,
        payloadAuthenticated = sessionContext.payloadAuthenticated,
        oem = oem
      )
    }
  }

  case class Request(
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    payload: ByteString,
    payloadType: PayloadType = PayloadType.Ipmi,
    encryptor: SessionContext.Encryptor,
    requestHashMaker: SessionContext.ReqHashMaker,
    payloadEncrypted: Boolean = false,
    payloadAuthenticated: Boolean = false,
    oem: Option[Oem] = None) extends IpmiSessionWrapperRequest {
    val authenticationType: AuthenticationType = AuthenticationType.RmcpPlus
  }

  object RawResponse {
    implicit def decoder: Decoder[RawResponse] = new Decoder[RawResponse] {

      def decode(data: ByteString): RawResponse = {
        def getBaseMessageAndTrailer(skipAndTrailer: ByteString): (ByteString, ByteString) = {
          val padLenAndTrailer = skipAndTrailer.dropWhile(_.toUnsignedInt == 0xff)

          val iterator = padLenAndTrailer.iterator
          val is = iterator.asInputStream

          val padLen = is.readByte.toUnsignedInt
          is.skip(1)

          require(padLen == skipAndTrailer.length - padLenAndTrailer.length, "Corrupted: Pad length does not equal bytes skipped")

          val trailer = iterator.toByteString
          val baseMessage = data.take(data.length - skipAndTrailer.length)

          (baseMessage, trailer)
        }

        val iterator = data.iterator
        val is = iterator.asInputStream

        val authType = is.read(1).as[AuthenticationType]

        val payloadByte = is.readByte
        val isPayloadEnc = payloadByte.bit7
        val isPayloadAuth = payloadByte.bit6

        val payloadType = payloadByte.as[PayloadType]

        val oem = if (payloadType == PayloadType.Oem) Some(is.read(6).as[Oem]) else None

        val sessId = is.read(4).as[ManagedSystemSessionId]
        val sessSeqNo = is.read(4).as[SessionSequenceNumber]

        val payloadLen = is.read(2).as[Short].toInt
        val encPayload = is.read(payloadLen)

        val baseMessageAndTrailer =
          if (sessId.isSession && authType != AuthenticationType.NoAuth &&
            !(authType == AuthenticationType.RmcpPlus && !isPayloadAuth))
            Some(getBaseMessageAndTrailer(iterator.toByteString))
          else None

        RawResponse(
          managedSystemSessionId = sessId,
          sessionSequenceNumber = sessSeqNo,
          encryptedPayload = encPayload,
          baseMessageAndTrailer = baseMessageAndTrailer,
          payloadType = payloadType,
          payloadEncrypted = isPayloadEnc,
          payloadAuthenticated = isPayloadAuth,
          oem = oem
        )
      }
    }
  }

  case class RawResponse(
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    encryptedPayload: ByteString,
    baseMessageAndTrailer: Option[(ByteString, ByteString)],
    payloadType: PayloadType = PayloadType.Ipmi,
    payloadEncrypted: Boolean = false,
    payloadAuthenticated: Boolean = false,
    oem: Option[Oem] = None) extends IpmiSessionWrapperRawResponse {
    val authenticationType: AuthenticationType = AuthenticationType.RmcpPlus

    def authenticatedResponse(sessionContext: SessionContext): IntegrityCheckError \/ Response = {
      val payload =
        if (payloadEncrypted)
          sessionContext.decryptor(encryptedPayload)
        else encryptedPayload

      val optErrMsg = for {
        (baseMessage, trailer) <- baseMessageAndTrailer
        hash <- sessionContext.responseHashMaker(managedSystemSessionId, sessionSequenceNumber, baseMessage)
        errorMsg <- (hash != trailer).option(s"Integrity check failed $hash != $trailer")
      } yield errorMsg

      optErrMsg match {
        case Some(msg) => IntegrityCheckError(msg).left
        case None      =>
          Response(
            managedSystemSessionId = managedSystemSessionId,
            sessionSequenceNumber = sessionSequenceNumber,
            payload = payload,
            payloadType = payloadType,
            oem = oem
          ).right
      }
    }
  }

  case class Response(
    managedSystemSessionId: ManagedSystemSessionId,
    sessionSequenceNumber: SessionSequenceNumber,
    payload: ByteString,
    payloadType: PayloadType = PayloadType.Ipmi,
    oem: Option[Oem] = None) extends IpmiSessionWrapperResponse

}


