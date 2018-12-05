package com.cyclone.ipmi.command.systemEventLog

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * SEL version number for a record
  */
case class SelVersion(major: Int, minor: Int)

object SelVersion {
  implicit val decoder: Decoder[SelVersion] = new Decoder[SelVersion] {

    def decode(data: ByteString) =
      SelVersion(data(0).bits0To3.toUnsignedInt, data(0).bits4To7.toUnsignedInt)
  }
}
