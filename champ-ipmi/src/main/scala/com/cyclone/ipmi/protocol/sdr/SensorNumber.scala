package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class SensorNumber(value: Int)

object SensorNumber {
  implicit val decoder: Codec[SensorNumber] = new Codec[SensorNumber] {
    def decode(data: ByteString) = SensorNumber(data(0).toUnsignedInt)

    def encode(a: SensorNumber) = ByteString(a.value.toByte)
  }
}
