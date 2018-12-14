package com.cyclone.ipmi.protocol.readingoffset

sealed trait BaseOsBootInstallationStatusEventOffset extends EventReadingOffset

object BaseOsBootInstallationStatusEventOffset {

  case object ABootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object CBootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object PXEBootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object DiagnosticBootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object CdRomBootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object RomBootCompleted extends BaseOsBootInstallationStatusEventOffset

  case object BootCompletedBootDeviceNotSpecified extends BaseOsBootInstallationStatusEventOffset

  case object BaseOsHypervisorInstallationStarted extends BaseOsBootInstallationStatusEventOffset

  case object BaseOsHypervisorInstallationCompleted extends BaseOsBootInstallationStatusEventOffset

  case object BaseOsHypervisorInstallationAborted extends BaseOsBootInstallationStatusEventOffset

  case object BaseOsHypervisorInstallationFailed extends BaseOsBootInstallationStatusEventOffset

  def offsetFor(bit: Int): Option[BaseOsBootInstallationStatusEventOffset] = bit match {
    case 0x00 => Some(ABootCompleted)
    case 0x01 => Some(CBootCompleted)
    case 0x02 => Some(PXEBootCompleted)
    case 0x03 => Some(DiagnosticBootCompleted)
    case 0x04 => Some(CdRomBootCompleted)
    case 0x05 => Some(RomBootCompleted)
    case 0x06 => Some(BootCompletedBootDeviceNotSpecified)
    case 0x07 => Some(BaseOsHypervisorInstallationStarted)
    case 0x08 => Some(BaseOsHypervisorInstallationCompleted)
    case 0x09 => Some(BaseOsHypervisorInstallationAborted)
    case 0x0a => Some(BaseOsHypervisorInstallationFailed)
    case _    => None
  }
}
