package com.cyclone.ipmi.protocol.readingoffset

sealed trait LimitEventOffset extends EventReadingOffset

object LimitEventOffset {

  case object LimitNotExceeded extends LimitEventOffset

  case object LimitExceeded extends LimitEventOffset

  def offsetFor(bit: Int): Option[LimitEventOffset] = bit match {
    case 0x00 => Some(LimitEventOffset.LimitNotExceeded)
    case 0x01 => Some(LimitEventOffset.LimitExceeded)
    case _    => None
  }
}
