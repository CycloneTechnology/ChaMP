package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class DeviceRevision(value: Int)

object DeviceRevision {
  implicit val decoder: Decoder[DeviceRevision] = new Decoder[DeviceRevision] {

    def decode(data: ByteString) =
      DeviceRevision(data(0).bits0To3.toUnsignedInt)
  }
}
