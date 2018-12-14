package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

case class SensorDataRecordId(lsb: Byte, msb: Byte)

object SensorDataRecordId {
  implicit val codec: Codec[SensorDataRecordId] = new Codec[SensorDataRecordId] {
    def encode(a: SensorDataRecordId) = ByteString(a.lsb, a.msb)

    def decode(data: ByteString) = SensorDataRecordId(data(0), data(1))
  }

  val first = SensorDataRecordId(0, 0)
  val last = SensorDataRecordId(0xff.toByte, 0xff.toByte)
}
