package com.cyclone.ipmi.protocol.packet

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec.{Coder, _}
import com.cyclone.ipmi.command.StatusCode

object SessionActivationCommandWrapper {

  object RequestPayload {
    implicit def coder: Coder[RequestPayload] = new Coder[RequestPayload] {

      def encode(payload: RequestPayload): ByteString = {
        import payload._

        val b = new ByteStringBuilder

        b += seqNo.toByte

        b ++= commandData.encode

        b.result()
      }
    }
  }

  case class RequestPayload(payloadType: PayloadType, seqNo: SeqNo, commandData: Codable) extends IpmiRequestPayload

  object ResponsePayload {
    implicit def decoder: Decoder[ResponsePayload] = new Decoder[ResponsePayload] {

      def decode(data: ByteString): ResponsePayload = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val seqNo = is.readByte.as[SeqNo]
        val statusCode = is.readByte.as[StatusCode]
        val bs = iterator.toByteString

        ResponsePayload(resultData = bs, statusCode = statusCode, seqNo = seqNo)
      }
    }
  }

  case class ResponsePayload(resultData: ByteString, statusCode: StatusCode, seqNo: SeqNo) extends IpmiResponsePayload

}
