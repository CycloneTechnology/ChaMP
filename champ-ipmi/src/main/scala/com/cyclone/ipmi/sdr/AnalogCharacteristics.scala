package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

case class AnalogCharacteristics(
  normalMinSpecified: Boolean,
  normalMaxSpecified: Boolean,
  nominalReadingSpecified: Boolean
)

object AnalogCharacteristics {
  implicit val decoder: Decoder[AnalogCharacteristics] = new Decoder[AnalogCharacteristics] {
    def decode(data: ByteString) =
      AnalogCharacteristics(
        normalMinSpecified = data(0).bit2,
        normalMaxSpecified = data(0).bit1,
        nominalReadingSpecified = data(0).bit1)
  }
}
