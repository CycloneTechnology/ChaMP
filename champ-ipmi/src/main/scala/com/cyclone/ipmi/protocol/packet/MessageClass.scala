package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

/**
  * Class of RMCP messages
  */
sealed trait MessageClass {
  def code: Byte
}


object MessageClass {
  implicit val codec: Codec[MessageClass] = new Codec[MessageClass] {
    def encode(a: MessageClass) =
      ByteString(a.code)

    def decode(data: ByteString): MessageClass =
      fromCode(data(0))
  }

  case object Asf extends MessageClass {
    val code: Byte = 6.toByte
  }

  case object Ipmi extends MessageClass {
    val code: Byte = 7.toByte
  }

  case object Oem extends MessageClass {
    val code: Byte = 8.toByte
  }

  case object Ack extends MessageClass {
    val code: Byte = 134.toByte
  }

  def fromCode(code: Byte): MessageClass = code match {
    case Asf.code  => Asf
    case Ipmi.code => Ipmi
    case Oem.code  => Oem
    case Ack.code  => Ack
  }
}