package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * SDR version number for a record
  */
case class SdrVersion(major: Int, minor: Int)

object SdrVersion {
  implicit val decoder: Decoder[SdrVersion] = new Decoder[SdrVersion] {

    def decode(data: ByteString) =
      SdrVersion(data(0).bits0To3.toUnsignedInt, data(0).bits4To7.toUnsignedInt)
  }
}
