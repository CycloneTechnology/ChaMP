package com.cyclone.ipmi.sdr.readingoffset

sealed trait OsStopShutdownEventOffset extends EventReadingOffset

object OsStopShutdownEventOffset {

  case object CriticalStopDuringOsLoadInitialization extends OsStopShutdownEventOffset

  case object RuntimeCriticalStop extends OsStopShutdownEventOffset

  case object OSGracefulStop extends OsStopShutdownEventOffset

  case object OSGracefulShutdown extends OsStopShutdownEventOffset

  case object SoftShutdownInitiatedByPEF extends OsStopShutdownEventOffset

  case object AgentNotResponding extends OsStopShutdownEventOffset

  def offsetFor(bit: Int): Option[OsStopShutdownEventOffset] = bit match {
    case 0x00 => Some(CriticalStopDuringOsLoadInitialization)
    case 0x01 => Some(RuntimeCriticalStop)
    case 0x02 => Some(OSGracefulStop)
    case 0x03 => Some(OSGracefulShutdown)
    case 0x04 => Some(SoftShutdownInitiatedByPEF)
    case 0x05 => Some(AgentNotResponding)
    case _    => None
  }
}
