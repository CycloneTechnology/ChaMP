package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Represents a raw sensor reading
  */
case class RawSensorValue(value: Int)

object RawSensorValue {
  implicit val decoder: Codec[RawSensorValue] = new Codec[RawSensorValue] {
    def decode(data: ByteString) = RawSensorValue(data(0).toUnsignedInt)

    def encode(a: RawSensorValue) = ByteString(a.value.toByte)
  }
}
