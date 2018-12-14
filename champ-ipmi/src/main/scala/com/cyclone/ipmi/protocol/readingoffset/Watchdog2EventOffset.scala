package com.cyclone.ipmi.protocol.readingoffset

sealed trait Watchdog2EventOffset extends EventReadingOffset

object Watchdog2EventOffset {

  case object TimerExpired extends Watchdog2EventOffset

  case object HardReset extends Watchdog2EventOffset

  case object PowerDown extends Watchdog2EventOffset

  case object PowerCycle extends Watchdog2EventOffset

  case object Reserved1 extends Watchdog2EventOffset

  case object Reserved2 extends Watchdog2EventOffset

  case object Reserved3 extends Watchdog2EventOffset

  case object Reserved4 extends Watchdog2EventOffset

  case object TimerInterrupt extends Watchdog2EventOffset

  def offsetFor(bit: Int): Option[Watchdog2EventOffset] = bit match {
    case 0x00 => Some(TimerExpired)
    case 0x01 => Some(HardReset)
    case 0x02 => Some(PowerDown)
    case 0x03 => Some(PowerCycle)
    case 0x04 => Some(Reserved1)
    case 0x05 => Some(Reserved2)
    case 0x06 => Some(Reserved3)
    case 0x07 => Some(Reserved4)
    case 0x08 => Some(TimerInterrupt)
    case _    => None
  }
}
