package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

/**
  * Represents one record in the MultiRecord Info Area
  */
trait MultiRecord

// TODO ^^ separate implementation for each record type...

object MultiRecord {
  implicit val decoder: Decoder[MultiRecord] = new Decoder[MultiRecord] {
    def decode(data: ByteString): MultiRecord = ???
  }
}
