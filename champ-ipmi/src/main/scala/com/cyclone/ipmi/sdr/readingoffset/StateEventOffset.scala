package com.cyclone.ipmi.sdr.readingoffset

sealed trait StateEventOffset extends EventReadingOffset

object StateEventOffset {

  case object StateDeasserted extends StateEventOffset

  case object StateAsserted extends StateEventOffset

  def offsetFor(bit: Int): Option[StateEventOffset] = bit match {
    case 0x00 => Some(StateEventOffset.StateDeasserted)
    case 0x01 => Some(StateEventOffset.StateAsserted)
    case _    => None
  }

}
