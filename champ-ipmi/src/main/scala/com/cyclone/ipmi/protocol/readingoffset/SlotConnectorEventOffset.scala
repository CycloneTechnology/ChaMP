package com.cyclone.ipmi.protocol.readingoffset

sealed trait SlotConnectorEventOffset extends EventReadingOffset

object SlotConnectorEventOffset {

  case object FaultyStatusAsserted extends SlotConnectorEventOffset

  case object IdentifyStatusAsserted extends SlotConnectorEventOffset

  case object SlotConnectorDeviceInstalledAttached extends SlotConnectorEventOffset

  case object SlotConnectorReadyForDeviceInstallation extends SlotConnectorEventOffset

  case object SlotConnectorReadyForDeviceRemoval extends SlotConnectorEventOffset

  case object SlotPowerIsOff extends SlotConnectorEventOffset

  case object SlotConnectorDeviceRemovalRequest extends SlotConnectorEventOffset

  case object InterlockAsserted extends SlotConnectorEventOffset

  case object SlotIsDisabled extends SlotConnectorEventOffset

  case object SlotHoldsSpareDevice extends SlotConnectorEventOffset

  def offsetFor(bit: Int): Option[SlotConnectorEventOffset] = bit match {
    case 0x00 => Some(FaultyStatusAsserted)
    case 0x01 => Some(IdentifyStatusAsserted)
    case 0x02 => Some(SlotConnectorDeviceInstalledAttached)
    case 0x03 => Some(SlotConnectorReadyForDeviceInstallation)
    case 0x04 => Some(SlotConnectorReadyForDeviceRemoval)
    case 0x05 => Some(SlotPowerIsOff)
    case 0x06 => Some(SlotConnectorDeviceRemovalRequest)
    case 0x07 => Some(InterlockAsserted)
    case 0x08 => Some(SlotIsDisabled)
    case 0x09 => Some(SlotHoldsSpareDevice)
    case _    => None
  }
}
