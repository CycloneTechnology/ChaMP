package com.cyclone.ipmi.protocol.readingoffset

sealed trait ProcessorEventOffset extends EventReadingOffset

object ProcessorEventOffset {

  case object Ierr extends ProcessorEventOffset

  case object ThermalTrip extends ProcessorEventOffset

  case object Frb1BistFailure extends ProcessorEventOffset

  case object Frb2HangInPostFailure extends ProcessorEventOffset

  case object Frb3ProcessorStartupInitializationFailure extends ProcessorEventOffset

  case object ConfigurationError extends ProcessorEventOffset

  case object SmbiosUncorrectableCpuComplexError extends ProcessorEventOffset

  case object ProcessorPresenceDetected extends ProcessorEventOffset

  case object ProcessorDisabled extends ProcessorEventOffset

  case object TerminatorPresenceDetected extends ProcessorEventOffset

  case object ProcessorAutomaticallyThrottled extends ProcessorEventOffset

  case object MachineCheckException extends ProcessorEventOffset

  case object CorrectableMachineCheckError extends ProcessorEventOffset

  def offsetFor(bit: Int): Option[ProcessorEventOffset] = bit match {
    case 0x00 => Some(Ierr)
    case 0x01 => Some(ThermalTrip)
    case 0x02 => Some(Frb1BistFailure)
    case 0x03 => Some(Frb2HangInPostFailure)
    case 0x04 => Some(Frb3ProcessorStartupInitializationFailure)
    case 0x05 => Some(ConfigurationError)
    case 0x06 => Some(SmbiosUncorrectableCpuComplexError)
    case 0x07 => Some(ProcessorPresenceDetected)
    case 0x08 => Some(ProcessorDisabled)
    case 0x09 => Some(TerminatorPresenceDetected)
    case 0x0a => Some(ProcessorAutomaticallyThrottled)
    case 0x0b => Some(MachineCheckException)
    case 0x0c => Some(CorrectableMachineCheckError)
    case _    => None
  }
}
