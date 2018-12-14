package com.cyclone.ipmi.protocol.readingoffset

sealed trait SeverityEventOffset extends EventReadingOffset

object SeverityEventOffset {

  case object TransitionToOK extends SeverityEventOffset

  case object TransitionToNonCriticalFromOK extends SeverityEventOffset

  case object TransitionToCriticalFromLessSevere extends SeverityEventOffset

  case object TransitionToNonRecoverableFromLessSevere extends SeverityEventOffset

  case object TransitionToNonCriticalFromMoreSevere extends SeverityEventOffset

  case object TransitionToCriticalFromNonRecoverable extends SeverityEventOffset

  case object TransitionToNonRecoverable extends SeverityEventOffset

  case object Monitor extends SeverityEventOffset

  case object Informational extends SeverityEventOffset

  def offsetFor(bit: Int): Option[SeverityEventOffset] = bit match {
    case 0x00 => Some(SeverityEventOffset.TransitionToOK)
    case 0x01 => Some(SeverityEventOffset.TransitionToNonCriticalFromOK)
    case 0x02 => Some(SeverityEventOffset.TransitionToCriticalFromLessSevere)
    case 0x03 => Some(SeverityEventOffset.TransitionToNonRecoverableFromLessSevere)
    case 0x04 => Some(SeverityEventOffset.TransitionToNonCriticalFromMoreSevere)
    case 0x05 => Some(SeverityEventOffset.TransitionToCriticalFromNonRecoverable)
    case 0x06 => Some(SeverityEventOffset.TransitionToNonRecoverable)
    case 0x07 => Some(SeverityEventOffset.Monitor)
    case 0x08 => Some(SeverityEventOffset.Informational)
    case _    => None
  }
}
