package com.cyclone.ipmi.protocol.readingoffset

sealed trait MemoryEventOffset extends EventReadingOffset

object MemoryEventOffset {

  case object CorrectableEccOtherCorrectableMemoryError extends MemoryEventOffset

  case object UncorrectableEccOtherUncorrectableMemoryError extends MemoryEventOffset

  case object Parity extends MemoryEventOffset

  case object MemoryScrubFailedStuckBit extends MemoryEventOffset

  case object MemoryDeviceDisabled extends MemoryEventOffset

  case object CorrectableEccOtherCorrectableMemoryErrorLoggingLimitReached extends MemoryEventOffset

  case object PresenceDetected extends MemoryEventOffset

  case object ConfigurationError extends MemoryEventOffset

  case object Spare extends MemoryEventOffset

  case object MemoryAutomaticallyThrottled extends MemoryEventOffset

  case object CriticalOvertemperature extends MemoryEventOffset

  def offsetFor(bit: Int): Option[MemoryEventOffset] = bit match {
    case 0x00 => Some(CorrectableEccOtherCorrectableMemoryError)
    case 0x01 => Some(UncorrectableEccOtherUncorrectableMemoryError)
    case 0x02 => Some(Parity)
    case 0x03 => Some(MemoryScrubFailedStuckBit)
    case 0x04 => Some(MemoryDeviceDisabled)
    case 0x05 => Some(CorrectableEccOtherCorrectableMemoryErrorLoggingLimitReached)
    case 0x06 => Some(PresenceDetected)
    case 0x07 => Some(ConfigurationError)
    case 0x08 => Some(Spare)
    case 0x09 => Some(MemoryAutomaticallyThrottled)
    case 0x0a => Some(CriticalOvertemperature)
    case _    => None
  }
}
