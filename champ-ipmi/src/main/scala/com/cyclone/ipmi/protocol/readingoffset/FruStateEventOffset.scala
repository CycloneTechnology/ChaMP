package com.cyclone.ipmi.protocol.readingoffset

sealed trait FruStateEventOffset extends EventReadingOffset

object FruStateEventOffset {

  case object FruNotInstalled extends FruStateEventOffset

  case object FruInactive extends FruStateEventOffset

  case object FruActivationRequested extends FruStateEventOffset

  case object FruActivationInProgress extends FruStateEventOffset

  case object FruActive extends FruStateEventOffset

  case object FruDeactivationRequested extends FruStateEventOffset

  case object FruDeactivationInProgress extends FruStateEventOffset

  case object FruCommunicationLost extends FruStateEventOffset

  def offsetFor(bit: Int): Option[FruStateEventOffset] = bit match {
    case 0x00 => Some(FruNotInstalled)
    case 0x01 => Some(FruInactive)
    case 0x02 => Some(FruActivationRequested)
    case 0x03 => Some(FruActivationInProgress)
    case 0x04 => Some(FruActive)
    case 0x05 => Some(FruDeactivationRequested)
    case 0x06 => Some(FruDeactivationInProgress)
    case 0x07 => Some(FruCommunicationLost)
    case _    => None
  }
}
