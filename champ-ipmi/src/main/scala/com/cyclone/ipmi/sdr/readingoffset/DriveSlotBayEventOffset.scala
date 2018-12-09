package com.cyclone.ipmi.sdr.readingoffset

sealed trait DriveSlotBayEventOffset extends EventReadingOffset

object DriveSlotBayEventOffset {

  case object DrivePresence extends DriveSlotBayEventOffset

  case object DriveFault extends DriveSlotBayEventOffset

  case object PredictiveFailure extends DriveSlotBayEventOffset

  case object HotSpare extends DriveSlotBayEventOffset

  case object ConsistencyCheckParityCheckInProgress extends DriveSlotBayEventOffset

  case object InCriticalArray extends DriveSlotBayEventOffset

  case object InFailedArray extends DriveSlotBayEventOffset

  case object RebuildRemapInProgress extends DriveSlotBayEventOffset

  case object RebuildRemapAbortedWasNotCompletedNormally extends DriveSlotBayEventOffset

  def offsetFor(bit: Int): Option[DriveSlotBayEventOffset] = bit match {
    case 0x00 => Some(DrivePresence)
    case 0x01 => Some(DriveFault)
    case 0x02 => Some(PredictiveFailure)
    case 0x03 => Some(HotSpare)
    case 0x04 => Some(ConsistencyCheckParityCheckInProgress)
    case 0x05 => Some(InCriticalArray)
    case 0x06 => Some(InFailedArray)
    case 0x07 => Some(RebuildRemapInProgress)
    case 0x08 => Some(RebuildRemapAbortedWasNotCompletedNormally)
    case _    => None
  }
}
