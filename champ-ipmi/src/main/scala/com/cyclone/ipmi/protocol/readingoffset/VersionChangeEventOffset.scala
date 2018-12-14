package com.cyclone.ipmi.protocol.readingoffset

sealed trait VersionChangeEventOffset extends EventReadingOffset

object VersionChangeEventOffset {

  case object HardwareChangeDetectedWithAssociatedEntity extends VersionChangeEventOffset

  case object FirmwareOrSoftwareChangeDetectedWithAssociatedEntity extends VersionChangeEventOffset

  case object HardwareIncompatibilityDetectedWithAssociatedEntity extends VersionChangeEventOffset

  case object FirmwareOrSoftwareIncompatibilityDetectedWithAssociatedEntity extends VersionChangeEventOffset

  case object EntityIsInvalidOrUnsupportedHardwareVersion extends VersionChangeEventOffset

  case object EntityContainsInvalidOrUnsupportedFirmwareOrSoftwareVersionWithAssociatedEntity
      extends VersionChangeEventOffset

  case object HardwareChangeDetectedWithAssociatedEntityWasSuccessful extends VersionChangeEventOffset

  case object SoftwareOrFirmwareChangeDetectedWithAssociatedEntity extends VersionChangeEventOffset

  def offsetFor(bit: Int): Option[VersionChangeEventOffset] = bit match {
    case 0x00 => Some(HardwareChangeDetectedWithAssociatedEntity)
    case 0x01 => Some(FirmwareOrSoftwareChangeDetectedWithAssociatedEntity)
    case 0x02 => Some(HardwareIncompatibilityDetectedWithAssociatedEntity)
    case 0x03 => Some(FirmwareOrSoftwareIncompatibilityDetectedWithAssociatedEntity)
    case 0x04 => Some(EntityIsInvalidOrUnsupportedHardwareVersion)
    case 0x05 =>
      Some(EntityContainsInvalidOrUnsupportedFirmwareOrSoftwareVersionWithAssociatedEntity)
    case 0x06 => Some(HardwareChangeDetectedWithAssociatedEntityWasSuccessful)
    case 0x07 => Some(SoftwareOrFirmwareChangeDetectedWithAssociatedEntity)
    case _    => None
  }
}
