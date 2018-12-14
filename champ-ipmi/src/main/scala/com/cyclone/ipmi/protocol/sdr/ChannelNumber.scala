package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class ChannelNumber(code: Int)

object ChannelNumber {
  implicit val decoder: Decoder[ChannelNumber] = new Decoder[ChannelNumber] {
    def decode(data: ByteString) = ChannelNumber(data(0).toUnsignedInt)
  }
}
