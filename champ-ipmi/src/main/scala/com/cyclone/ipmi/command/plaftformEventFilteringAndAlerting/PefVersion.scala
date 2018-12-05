package com.cyclone.ipmi.command.plaftformEventFilteringAndAlerting

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * PEF version number for a record
  */
case class PefVersion(major: Int, minor: Int)

object PefVersion {
  implicit val decoder: Decoder[PefVersion] = new Decoder[PefVersion] {

    def decode(data: ByteString) =
      PefVersion(data(0).bits0To3.toUnsignedInt, data(0).bits4To7.toUnsignedInt)
  }
}
