package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

case class SensorOwnerLun(value: Byte)

object SensorOwnerLun {
  implicit val decoder: Decoder[SensorOwnerLun] = new Decoder[SensorOwnerLun] {
    def decode(data: ByteString) = SensorOwnerLun(data(0))
  }
}
