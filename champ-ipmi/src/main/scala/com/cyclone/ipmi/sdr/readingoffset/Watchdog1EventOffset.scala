package com.cyclone.ipmi.sdr.readingoffset

sealed trait Watchdog1EventOffset extends EventReadingOffset

object Watchdog1EventOffset {

  case object BiosWatchdogReset extends Watchdog1EventOffset

  case object OsWatchdogReset extends Watchdog1EventOffset

  case object OsWatchdogShutDown extends Watchdog1EventOffset

  case object OsWatchdogPowerDown extends Watchdog1EventOffset

  case object OsWatchdogPowerCycle extends Watchdog1EventOffset

  case object OsWatchdogNmiDiagnosticInterrupt extends Watchdog1EventOffset

  case object OsWatchdogExpiredStatusOnly extends Watchdog1EventOffset

  case object OsWatchdogPreTimeoutInterruptNonNMI extends Watchdog1EventOffset

  def offsetFor(bit: Int): Option[Watchdog1EventOffset] = bit match {
    case 0x00 => Some(BiosWatchdogReset)
    case 0x01 => Some(OsWatchdogReset)
    case 0x02 => Some(OsWatchdogShutDown)
    case 0x03 => Some(OsWatchdogPowerDown)
    case 0x04 => Some(OsWatchdogPowerCycle)
    case 0x05 => Some(OsWatchdogNmiDiagnosticInterrupt)
    case 0x06 => Some(OsWatchdogExpiredStatusOnly)
    case 0x07 => Some(OsWatchdogPreTimeoutInterruptNonNMI)
    case _    => None
  }
}
