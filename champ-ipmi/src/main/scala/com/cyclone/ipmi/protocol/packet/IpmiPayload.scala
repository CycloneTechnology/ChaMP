package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.UnsupportedPayloadType
import com.cyclone.ipmi.codec.Codable
import com.cyclone.ipmi.command.StatusCode
import scalaz.Scalaz._

object IpmiPayload {

  def decode(data: ByteString, payloadType: PayloadType): IpmiErrorOr[IpmiResponsePayload] =
    payloadType match {
      case PayloadType.Ipmi =>
        StandardCommandWrapper.ResponsePayload.decoder.handleExceptions.decode(data)

      case PayloadType.OpenSessionResp | PayloadType.Rakp2 | PayloadType.Rakp4 =>
        SessionActivationCommandWrapper.ResponsePayload.decoder.handleExceptions.decode(data)
      case _ => UnsupportedPayloadType(payloadType).left
    }
}

sealed trait IpmiPayload {
  def seqNo: SeqNo
}

trait IpmiRequestPayload extends IpmiPayload {
  def commandData: Codable

  def payloadType: PayloadType
}

trait IpmiResponsePayload extends IpmiPayload {
  def resultData: ByteString

  def statusCode: StatusCode
}
