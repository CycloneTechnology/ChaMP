package com.cyclone.ipmi.protocol.readingoffset

sealed trait StatusEventOffset extends EventReadingOffset

object StatusEventOffset {

  case object TransitionToRunning extends StatusEventOffset

  case object TransitionToInTest extends StatusEventOffset

  case object TransitionToPowerOff extends StatusEventOffset

  case object TransitionToOnLine extends StatusEventOffset

  case object TransitionToOffLine extends StatusEventOffset

  case object TransitionToOffDuty extends StatusEventOffset

  case object TransitionToDegraded extends StatusEventOffset

  case object TransitionToPowerSave extends StatusEventOffset

  case object InstallError extends StatusEventOffset

  def offsetFor(bit: Int): Option[StatusEventOffset] = bit match {
    case 0x00 => Some(StatusEventOffset.TransitionToRunning)
    case 0x01 => Some(StatusEventOffset.TransitionToInTest)
    case 0x02 => Some(StatusEventOffset.TransitionToPowerOff)
    case 0x03 => Some(StatusEventOffset.TransitionToOnLine)
    case 0x04 => Some(StatusEventOffset.TransitionToOffLine)
    case 0x05 => Some(StatusEventOffset.TransitionToOffDuty)
    case 0x06 => Some(StatusEventOffset.TransitionToDegraded)
    case 0x07 => Some(StatusEventOffset.TransitionToPowerSave)
    case 0x08 => Some(StatusEventOffset.InstallError)
    case _    => None
  }
}
