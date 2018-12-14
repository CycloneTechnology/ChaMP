package com.cyclone.ipmi.protocol.readingoffset

sealed trait EventLoggingDisabledEventOffset extends EventReadingOffset

object EventLoggingDisabledEventOffset {

  case object CorrectableMemoryErrorLoggingDisabled extends EventLoggingDisabledEventOffset

  case object EventTypeLoggingDisabled extends EventLoggingDisabledEventOffset

  case object LogAreaResetCleared extends EventLoggingDisabledEventOffset

  case object AllEventLoggingDisabled extends EventLoggingDisabledEventOffset

  case object SelFull extends EventLoggingDisabledEventOffset

  case object SelAlmostFull extends EventLoggingDisabledEventOffset

  case object CorrectableMachineCheckErrorLoggingDisabled extends EventLoggingDisabledEventOffset

  def offsetFor(bit: Int): Option[EventLoggingDisabledEventOffset] = bit match {
    case 0x00 => Some(CorrectableMemoryErrorLoggingDisabled)
    case 0x01 => Some(EventTypeLoggingDisabled)
    case 0x02 => Some(LogAreaResetCleared)
    case 0x03 => Some(AllEventLoggingDisabled)
    case 0x04 => Some(SelFull)
    case 0x05 => Some(SelAlmostFull)
    case 0x06 => Some(CorrectableMachineCheckErrorLoggingDisabled)
    case _    => None
  }
}
