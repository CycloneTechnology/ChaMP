package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Codec

case class SdrReservationId(lsb: Byte, msb: Byte)

object SdrReservationId {
  implicit val codec: Codec[SdrReservationId] = new Codec[SdrReservationId] {
    def encode(a: SdrReservationId) = ByteString(a.lsb, a.msb)

    def decode(data: ByteString) = SdrReservationId(data(0), data(1))
  }

  val noReservation = SdrReservationId(0, 0)
}
