package com.cyclone.ipmi.sdr.readingoffset

sealed trait BootErrorEventOffset extends EventReadingOffset

object BootErrorEventOffset {

  case object NoBootableMedia extends BootErrorEventOffset

  case object NonBootableDisketteLeftInDrive extends BootErrorEventOffset

  case object PXEServerNotFound extends BootErrorEventOffset

  case object InvalidBootSector extends BootErrorEventOffset

  case object TimeoutWaitingForUserSelectionOfBootSource extends BootErrorEventOffset

  def offsetFor(bit: Int): Option[BootErrorEventOffset] = bit match {
    case 0x00 => Some(NoBootableMedia)
    case 0x01 => Some(NonBootableDisketteLeftInDrive)
    case 0x02 => Some(PXEServerNotFound)
    case 0x03 => Some(InvalidBootSector)
    case 0x04 => Some(TimeoutWaitingForUserSelectionOfBootSource)
    case _    => None
  }
}
