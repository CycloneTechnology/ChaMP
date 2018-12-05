package com.cyclone.ipmi.command.oem

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * LanSource
  */
sealed trait LanSource

object LanSource {

  implicit val decoder: Decoder[LanSource] = new Decoder[LanSource] {
    def decode(data: ByteString): LanSource = data(0).toUnsignedInt match {
      case 0x00 => SharedNic
      case 0x01 => DedicatedNic
    }
  }

  implicit val encoder: Coder[LanSource] = new Coder[LanSource] {
    def encode(a: LanSource): ByteString = {
      a match {
        case SharedNic    => ByteString(0x00)
        case DedicatedNic => ByteString(0x01)
      }
    }
  }

  case object SharedNic extends LanSource

  case object DedicatedNic extends LanSource

}
