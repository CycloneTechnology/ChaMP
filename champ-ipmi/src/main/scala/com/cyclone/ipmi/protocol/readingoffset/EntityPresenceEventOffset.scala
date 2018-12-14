package com.cyclone.ipmi.protocol.readingoffset

sealed trait EntityPresenceEventOffset extends EventReadingOffset

object EntityPresenceEventOffset {

  case object EntityPresent extends EntityPresenceEventOffset

  case object EntityAbsent extends EntityPresenceEventOffset

  case object EntityDisabled extends EntityPresenceEventOffset

  def offsetFor(bit: Int): Option[EntityPresenceEventOffset] = bit match {
    case 0x00 => Some(EntityPresent)
    case 0x01 => Some(EntityAbsent)
    case 0x02 => Some(EntityDisabled)
    case _    => None
  }
}
