package com.cyclone.ipmi.sdr.readingoffset

sealed trait PresenceEventOffset extends EventReadingOffset

object PresenceEventOffset {

  case object DeviceRemovedDeviceAbsent extends PresenceEventOffset

  case object DeviceInsertedDevicePresent extends PresenceEventOffset

  def offsetFor(bit: Int): Option[PresenceEventOffset] = bit match {
    case 0x00 => Some(PresenceEventOffset.DeviceRemovedDeviceAbsent)
    case 0x01 => Some(PresenceEventOffset.DeviceInsertedDevicePresent)
    case _    => None
  }

}
