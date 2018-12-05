package com.cyclone.ipmi.command.oem.fujitsu.common.power

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait InhibitFlag

object InhibitFlag {
  implicit val decoder: Decoder[InhibitFlag] = new Decoder[InhibitFlag] {

    def decode(data: ByteString): InhibitFlag = data(0).toUnsignedInt match {
      case 0x00 => NoInhibit
      case 0x01 => Inhibit
    }
  }

  implicit val encoder: Coder[InhibitFlag] = new Coder[InhibitFlag] {

    def encode(a: InhibitFlag): ByteString = {
      a match {
        case NoInhibit => ByteString(0x00)
        case Inhibit   => ByteString(0x01)
      }
    }
  }

  case object NoInhibit extends InhibitFlag

  case object Inhibit extends InhibitFlag

}
