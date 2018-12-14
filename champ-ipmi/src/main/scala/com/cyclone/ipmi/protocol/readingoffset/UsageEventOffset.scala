package com.cyclone.ipmi.protocol.readingoffset

sealed trait UsageEventOffset extends EventReadingOffset

object UsageEventOffset {

  case object TransitionToIdle extends UsageEventOffset

  case object TransitionToActive extends UsageEventOffset

  case object TransitionToBusy extends UsageEventOffset

  def offsetFor(bit: Int): Option[UsageEventOffset] = bit match {
    case 0x00 => Some(UsageEventOffset.TransitionToIdle)
    case 0x01 => Some(UsageEventOffset.TransitionToActive)
    case 0x02 => Some(UsageEventOffset.TransitionToBusy)
    case _    => None
  }

}
