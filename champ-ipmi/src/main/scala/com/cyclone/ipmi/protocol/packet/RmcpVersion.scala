package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

/**
  * RMCP version
  */
sealed trait RmcpVersion {
  def code: Byte
}

object RmcpVersion {

  implicit val codec: Codec[RmcpVersion] = new Codec[RmcpVersion] {
    def encode(a: RmcpVersion) =
      ByteString(a.code)

    def decode(data: ByteString): RmcpVersion =
      fromCode(data(0))
  }

  case object RMCP1_0 extends RmcpVersion {
    val code: Byte = 0x06.toByte
  }

  def fromCode(code: Byte): RmcpVersion = code match {
    case RMCP1_0.code => RMCP1_0
  }

}