package com.cyclone.ipmi.sdr.readingoffset

sealed trait EnablementEventOffset extends EventReadingOffset

object EnablementEventOffset {

  case object DeviceDisabled extends EnablementEventOffset

  case object DeviceEnabled extends EnablementEventOffset

  def offsetFor(bit: Int): Option[EnablementEventOffset] = bit match {
    case 0x00 => Some(EnablementEventOffset.DeviceDisabled)
    case 0x01 => Some(EnablementEventOffset.DeviceEnabled)
    case _    => None
  }
}
