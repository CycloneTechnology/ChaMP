package com.cyclone.ipmi.protocol.readingoffset

sealed trait ACPIPowerEventOffset extends EventReadingOffset

object ACPIPowerEventOffset {

  case object D0PowerState extends ACPIPowerEventOffset

  case object D1PowerState extends ACPIPowerEventOffset

  case object D2PowerState extends ACPIPowerEventOffset

  case object D3PowerState extends ACPIPowerEventOffset

  def offsetFor(bit: Int): Option[ACPIPowerEventOffset] = bit match {
    case 0x00 => Some(ACPIPowerEventOffset.D0PowerState)
    case 0x01 => Some(ACPIPowerEventOffset.D1PowerState)
    case 0x02 => Some(ACPIPowerEventOffset.D2PowerState)
    case 0x03 => Some(ACPIPowerEventOffset.D3PowerState)
    case _    => None
  }
}
