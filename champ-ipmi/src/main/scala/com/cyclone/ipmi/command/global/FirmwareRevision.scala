package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class FirmwareRevision(major: Int, minor: Int)

object FirmwareRevision {
  implicit val decoder: Decoder[FirmwareRevision] = new Decoder[FirmwareRevision] {
    def decode(data: ByteString) =
      FirmwareRevision(data(0).bits0To6.toUnsignedInt, data(1).toUnsignedInt)
  }
}