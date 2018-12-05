package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait SensorDirection

object SensorDirection {
  // NOTE bits 0 and 1 in full record but 6 and 7 in shared record.
  // Assume conversion to the former...
  implicit val decoder: Decoder[SensorDirection] = new Decoder[SensorDirection] {
    def decode(data: ByteString): SensorDirection = data(0).bits0To1.toUnsignedInt match {
      case 0 => Unspecified
      case 1 => Input
      case 2 => Output
      case _ => Unspecified
    }
  }

  case object Unspecified extends SensorDirection

  case object Input extends SensorDirection

  case object Output extends SensorDirection

}
