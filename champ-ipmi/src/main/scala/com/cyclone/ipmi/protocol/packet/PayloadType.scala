package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait PayloadType {
  def code: Byte
}

object PayloadType {

  implicit val codec: Codec[PayloadType] = new Codec[PayloadType] {
    def encode(a: PayloadType) =
      ByteString(PayloadType.Ipmi.code)

    def decode(data: ByteString): PayloadType =
      fromCode(data(0).bits0To5)
  }

  case object Ipmi extends PayloadType {
    val code: Byte = 0.toByte
  }

  case object Sol extends PayloadType {
    val code: Byte = 1.toByte
  }

  case object Oem extends PayloadType {
    val code: Byte = 2.toByte
  }

  case object OpenSessionReq extends PayloadType {
    val code: Byte = 16.toByte
  }

  case object OpenSessionResp extends PayloadType {
    val code: Byte = 17.toByte
  }

  case object Rakp1 extends PayloadType {
    val code: Byte = 18.toByte
  }

  case object Rakp2 extends PayloadType {
    val code: Byte = 19.toByte
  }

  case object Rakp3 extends PayloadType {
    val code: Byte = 20.toByte
  }

  case object Rakp4 extends PayloadType {
    val code: Byte = 21.toByte
  }

  case class ExtraOem(code: Byte) extends PayloadType

  def fromCode(code: Byte): PayloadType = code match {
    case Ipmi.code            => Ipmi
    case Sol.code             => Sol
    case Oem.code             => Oem
    case OpenSessionReq.code  => OpenSessionReq
    case OpenSessionResp.code => OpenSessionResp
    case Rakp1.code           => Rakp1
    case Rakp2.code           => Rakp2
    case Rakp3.code           => Rakp3
    case Rakp4.code           => Rakp4
    case _                    => ExtraOem(code)
  }
}
