package com.cyclone.ipmi.sdr.readingoffset

sealed trait ChipSetEventOffset extends EventReadingOffset

object ChipSetEventOffset {

  case object SoftPowerControlFailure extends ChipSetEventOffset

  case object ThermalTrip extends ChipSetEventOffset

  def offsetFor(bit: Int): Option[ChipSetEventOffset] = bit match {
    case 0x00 => Some(SoftPowerControlFailure)
    case 0x01 => Some(ThermalTrip)
    case _    => None
  }
}
