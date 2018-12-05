package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait AnalogDataFormat

object AnalogDataFormat {
  implicit val decoder: Decoder[AnalogDataFormat] = new Decoder[AnalogDataFormat] {

    def decode(data: ByteString): AnalogDataFormat = data(0).bits6To7.toUnsignedInt match {
      case 0 => Unsigned
      case 1 => OnesComplement
      case 2 => TwosComplement
      case 3 => NonAnalog
    }
  }

  case object Unsigned extends AnalogDataFormat

  case object OnesComplement extends AnalogDataFormat

  case object TwosComplement extends AnalogDataFormat

  case object NonAnalog extends AnalogDataFormat

}
