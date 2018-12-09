package com.cyclone.ipmi.sdr.readingoffset

sealed trait PlatformAlertEventOffset extends EventReadingOffset

object PlatformAlertEventOffset {

  case object PlatformGeneratedPage extends PlatformAlertEventOffset

  case object PlatformGeneratedLanAlert extends PlatformAlertEventOffset

  case object PlatformEventTrapGenerated extends PlatformAlertEventOffset

  case object PlatformGeneratedSnmpTrap extends PlatformAlertEventOffset

  def offsetFor(bit: Int): Option[PlatformAlertEventOffset] = bit match {
    case 0x00 => Some(PlatformGeneratedPage)
    case 0x01 => Some(PlatformGeneratedLanAlert)
    case 0x02 => Some(PlatformEventTrapGenerated)
    case 0x03 => Some(PlatformGeneratedSnmpTrap)
    case _    => None
  }
}
