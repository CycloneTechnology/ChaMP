package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait FirmwareSelector

object FirmwareSelector {
  implicit val decoder: Decoder[FirmwareSelector] = new Decoder[FirmwareSelector] {

    def decode(data: ByteString): FirmwareSelector = data(0).toUnsignedInt match {
      case 0x00 => Auto
      case 0x01 => LowFirmwareImage
      case 0x02 => HighFirmwareImage
      case 0x03 => AutoOldestVersion
      case 0x04 => MostRecentlyProgrammedFirmware
      case 0x05 => LeastRecentlyProgrammedFirmware
    }
  }

  implicit val encoder: Coder[FirmwareSelector] = new Coder[FirmwareSelector] {

    def encode(a: FirmwareSelector): ByteString = {
      a match {
        case Auto                            => ByteString(0x00)
        case LowFirmwareImage                => ByteString(0x01)
        case HighFirmwareImage               => ByteString(0x02)
        case AutoOldestVersion               => ByteString(0x03)
        case MostRecentlyProgrammedFirmware  => ByteString(0x04)
        case LeastRecentlyProgrammedFirmware => ByteString(0x05)
      }
    }
  }

  case object Auto extends FirmwareSelector

  case object LowFirmwareImage extends FirmwareSelector

  case object HighFirmwareImage extends FirmwareSelector

  case object AutoOldestVersion extends FirmwareSelector

  case object MostRecentlyProgrammedFirmware extends FirmwareSelector

  case object LeastRecentlyProgrammedFirmware extends FirmwareSelector

}
