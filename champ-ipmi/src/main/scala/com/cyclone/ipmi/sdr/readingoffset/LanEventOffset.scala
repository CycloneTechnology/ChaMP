package com.cyclone.ipmi.sdr.readingoffset

sealed trait LanEventOffset extends EventReadingOffset

object LanEventOffset {

  case object LanHeartbeatLost extends LanEventOffset

  case object LanHeartbeat extends LanEventOffset

  def offsetFor(bit: Int): Option[LanEventOffset] = bit match {
    case 0x00 => Some(LanHeartbeatLost)
    case 0x01 => Some(LanHeartbeat)
    case _    => None
  }
}
