package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

// TODO interpret bytes...?
case class SensorInitialization(value: Byte)

object SensorInitialization {
  implicit val decoder: Decoder[SensorInitialization] = new Decoder[SensorInitialization] {
    def decode(data: ByteString) = SensorInitialization(data(0))
  }
}
