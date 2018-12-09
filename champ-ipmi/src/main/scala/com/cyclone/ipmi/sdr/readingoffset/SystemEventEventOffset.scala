package com.cyclone.ipmi.sdr.readingoffset

sealed trait SystemEventEventOffset extends EventReadingOffset

object SystemEventEventOffset {

  case object SystemReconfigured extends SystemEventEventOffset

  case object OEMSystemBootEvent extends SystemEventEventOffset

  case object UndeterminedSystemHardwareFailure extends SystemEventEventOffset

  case object EntryAddedToAuxiliaryLog extends SystemEventEventOffset

  case object PEFAction extends SystemEventEventOffset

  case object TimestampClockSynch extends SystemEventEventOffset

  def offsetFor(bit: Int): Option[SystemEventEventOffset] = bit match {
    case 0x00 => Some(SystemReconfigured)
    case 0x01 => Some(OEMSystemBootEvent)
    case 0x02 => Some(UndeterminedSystemHardwareFailure)
    case 0x03 => Some(EntryAddedToAuxiliaryLog)
    case 0x04 => Some(PEFAction)
    case 0x05 => Some(TimestampClockSynch)
    case _    => None
  }
}
