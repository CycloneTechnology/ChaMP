package com.cyclone.ipmi.protocol.readingoffset

sealed trait CriticalInterruptEventOffset extends EventReadingOffset

object CriticalInterruptEventOffset {

  case object FrontPanelNmiDiagnosticInterrupt extends CriticalInterruptEventOffset

  case object BusTimeout extends CriticalInterruptEventOffset

  case object IoChannelCheckNmi extends CriticalInterruptEventOffset

  case object SoftwareNmi extends CriticalInterruptEventOffset

  case object PciPerr extends CriticalInterruptEventOffset

  case object PciSerr extends CriticalInterruptEventOffset

  case object EisaFailSafeTimeout extends CriticalInterruptEventOffset

  case object BusCorrectableError extends CriticalInterruptEventOffset

  case object BusUncorrectableError extends CriticalInterruptEventOffset

  case object FatalNmi extends CriticalInterruptEventOffset

  case object BusFatalError extends CriticalInterruptEventOffset

  case object BusDegraded extends CriticalInterruptEventOffset

  def offsetFor(bit: Int): Option[CriticalInterruptEventOffset] = bit match {
    case 0x00 => Some(FrontPanelNmiDiagnosticInterrupt)
    case 0x01 => Some(BusTimeout)
    case 0x02 => Some(IoChannelCheckNmi)
    case 0x03 => Some(SoftwareNmi)
    case 0x04 => Some(PciPerr)
    case 0x05 => Some(PciSerr)
    case 0x06 => Some(EisaFailSafeTimeout)
    case 0x07 => Some(BusCorrectableError)
    case 0x08 => Some(BusUncorrectableError)
    case 0x09 => Some(FatalNmi)
    case 0x0a => Some(BusFatalError)
    case 0x0b => Some(BusDegraded)
    case _    => None
  }
}
