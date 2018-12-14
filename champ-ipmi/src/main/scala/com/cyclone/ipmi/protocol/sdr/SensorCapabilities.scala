package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

// TODO interpret bytes...?
case class SensorCapabilities(value: Byte)

object SensorCapabilities {
  implicit val decoder: Decoder[SensorCapabilities] = new Decoder[SensorCapabilities] {
    def decode(data: ByteString) = SensorCapabilities(data(0))
  }
}
