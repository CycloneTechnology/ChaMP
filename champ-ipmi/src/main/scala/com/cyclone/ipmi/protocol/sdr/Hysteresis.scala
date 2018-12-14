package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait Hysteresis

object Hysteresis {
  implicit val decoder: Decoder[Hysteresis] = new Decoder[Hysteresis] {

    def decode(data: ByteString): Hysteresis = {
      val value = data(0).toUnsignedInt

      if (value == 0)
        NoHysteresis
      else
        HysteresisValue(value)
    }
  }

  case object NoHysteresis extends Hysteresis

  case class HysteresisValue(rawValue: Int) extends Hysteresis

}
