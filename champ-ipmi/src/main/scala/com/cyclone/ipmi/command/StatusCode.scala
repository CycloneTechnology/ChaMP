package com.cyclone.ipmi.command

import akka.util.ByteString
import com.cyclone.ipmi.codec.{Coder, Decoder}

object StatusCode {
  implicit val coder: Coder[StatusCode] = new Coder[StatusCode] {
    def encode(a: StatusCode) = ByteString(a.code)
  }

  def apply(intCode: Int): StatusCode = StatusCode(intCode.toByte)

  implicit val decoder: Decoder[StatusCode] = new Decoder[StatusCode] {
    def decode(data: ByteString) = StatusCode(data(0))
  }

  val NoErrors = StatusCode(0)
}

/**
  * Holds a raw status/completion code from a ICMP response.
  *
  * These are interpreted and converted to error messages depending on the request (i.e.
  * which command) that is being run.
  */
case class StatusCode(code: Byte) extends AnyVal {
  override def toString: String = "StatusCode(0x%02X)".format(code & 0xff)

  def isError: Boolean = code != 0
}
