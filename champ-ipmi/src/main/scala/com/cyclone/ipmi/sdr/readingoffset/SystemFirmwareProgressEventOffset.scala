package com.cyclone.ipmi.sdr.readingoffset

sealed trait SystemFirmwareProgressEventOffset extends EventReadingOffset

object SystemFirmwareProgressEventOffset {

  case object FirmwareErrorPostError extends SystemFirmwareProgressEventOffset

  case object FirmwareHang extends SystemFirmwareProgressEventOffset

  case object FirmwareProgress extends SystemFirmwareProgressEventOffset

  def offsetFor(bit: Int): Option[SystemFirmwareProgressEventOffset] = bit match {
    case 0x00 => Some(FirmwareErrorPostError)
    case 0x01 => Some(FirmwareHang)
    case 0x02 => Some(FirmwareProgress)
    case _    => None
  }
}
