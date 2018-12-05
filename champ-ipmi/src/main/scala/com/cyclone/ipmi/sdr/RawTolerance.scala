package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class RawTolerance(value: Int)

object RawTolerance {
  implicit val decoder: Decoder[RawTolerance] = new Decoder[RawTolerance] {
    def decode(data: ByteString) = RawTolerance(data(0).bits0To5.toUnsignedInt)
  }
}