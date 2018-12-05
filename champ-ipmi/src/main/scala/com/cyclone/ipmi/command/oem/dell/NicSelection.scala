package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * NicSelection
  */
sealed trait NicSelection

object NicSelection {

  implicit val decoder: Decoder[NicSelection] = new Decoder[NicSelection] {

    def decode(data: ByteString): NicSelection = data(0).toUnsignedInt match {
      case 0x00 => Shared
      case 0x01 => SharedWithFailoverToNic2
      case 0x02 => Dedicated
      case 0x03 => SharedWithFailoverToAllNics
    }
  }

  implicit val encoder: Coder[NicSelection] = new Coder[NicSelection] {

    def encode(a: NicSelection): ByteString = {
      a match {
        case Shared                      => ByteString(0x00)
        case SharedWithFailoverToNic2    => ByteString(0x01)
        case Dedicated                   => ByteString(0x02)
        case SharedWithFailoverToAllNics => ByteString(0x03)
      }
    }
  }

  case object Shared extends NicSelection

  case object SharedWithFailoverToNic2 extends NicSelection

  case object Dedicated extends NicSelection

  case object SharedWithFailoverToAllNics extends NicSelection

}
