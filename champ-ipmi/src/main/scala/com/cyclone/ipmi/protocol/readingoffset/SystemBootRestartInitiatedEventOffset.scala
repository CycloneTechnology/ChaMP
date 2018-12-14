package com.cyclone.ipmi.protocol.readingoffset

sealed trait SystemBootRestartInitiatedEventOffset extends EventReadingOffset

object SystemBootRestartInitiatedEventOffset {

  case object InitiatedByPowerUp extends SystemBootRestartInitiatedEventOffset

  case object InitiatedByHardReset extends SystemBootRestartInitiatedEventOffset

  case object InitiatedByWarmReset extends SystemBootRestartInitiatedEventOffset

  case object UserRequestedPXEBoot extends SystemBootRestartInitiatedEventOffset

  case object AutomaticBootToDiagnostic extends SystemBootRestartInitiatedEventOffset

  case object OSRuntimeSoftwareInitiatedHardReset extends SystemBootRestartInitiatedEventOffset

  case object OSRuntimeSoftwareInitiatedWarmReset extends SystemBootRestartInitiatedEventOffset

  case object SystemRestart extends SystemBootRestartInitiatedEventOffset

  def offsetFor(bit: Int): Option[SystemBootRestartInitiatedEventOffset] = bit match {
    case 0x00 => Some(InitiatedByPowerUp)
    case 0x01 => Some(InitiatedByHardReset)
    case 0x02 => Some(InitiatedByWarmReset)
    case 0x03 => Some(UserRequestedPXEBoot)
    case 0x04 => Some(AutomaticBootToDiagnostic)
    case 0x05 => Some(OSRuntimeSoftwareInitiatedHardReset)
    case 0x06 => Some(OSRuntimeSoftwareInitiatedWarmReset)
    case 0x07 => Some(SystemRestart)
    case _    => None
  }
}
