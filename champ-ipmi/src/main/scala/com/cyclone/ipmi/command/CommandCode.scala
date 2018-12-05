package com.cyclone.ipmi.command

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

case class CommandCode(code: Int) extends AnyVal {
  override def toString: String = "CommandCode(0x%02X)".format(code & 0xff)
}

object CommandCode {
  implicit val codec: Codec[CommandCode] = new Codec[CommandCode] {

    def encode(a: CommandCode) =
      ByteString(a.code)

    def decode(data: ByteString) =
      CommandCode(data(0))
  }
}
