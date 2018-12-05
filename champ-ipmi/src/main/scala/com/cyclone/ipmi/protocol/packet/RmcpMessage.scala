package com.cyclone.ipmi.protocol.packet

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec._

import scalaz.Scalaz._

object RmcpMessage {

  def encode(message: RmcpMessage): ByteString =
    coder.encode(message)

  def decode(data: ByteString): IpmiErrorOr[RmcpMessage] =
    decoder.handleExceptions.decode(data)

  def coder: Coder[RmcpMessage] = new Coder[RmcpMessage] {

    def encode(message: RmcpMessage): ByteString = {
      import message._

      val b = new ByteStringBuilder

      b ++= version.toBin
      b += 0
      b += sequenceNumber
      b ++= messageClass.toBin

      b ++= sessionWrapper

      b.result()
    }
  }

  def decoder: Decoder[RmcpMessage] = new Decoder[RmcpMessage] {

    def decode(data: ByteString): RmcpMessage = {

      val iterator = data.iterator
      val is = iterator.asInputStream

      val version = is.readByte.as[RmcpVersion]
      is.skip(1) // ignore reserved
      val seq = is.readByte
      val msgClass = is.readByte.as[MessageClass]

      val sesWrapper = iterator.toByteString

      RmcpMessage(
        sessionWrapper = sesWrapper,
        messageClass = msgClass,
        version = version,
        sequenceNumber = seq
      )
    }
  }
}

/**
  * Representation of an RMCP message
  */
case class RmcpMessage(
  sessionWrapper: ByteString,
  messageClass: MessageClass = MessageClass.Ipmi,
  version: RmcpVersion = RmcpVersion.RMCP1_0,
  sequenceNumber: Byte = 0xff.toByte // See RMCP ACK Messages - no ack generated
)
