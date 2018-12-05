package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class IpmiVersion(major: Int, minor: Int)

object IpmiVersion {
  implicit val decoder: Decoder[IpmiVersion] = new Decoder[IpmiVersion] {
    def decode(data: ByteString) =
      IpmiVersion(data(0).bits0To3.toUnsignedInt, data(0).bits4To7.toUnsignedInt)
  }
}