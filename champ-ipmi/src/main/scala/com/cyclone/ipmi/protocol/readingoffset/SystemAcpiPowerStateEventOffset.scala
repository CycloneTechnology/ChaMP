package com.cyclone.ipmi.protocol.readingoffset

sealed trait SystemAcpiPowerStateEventOffset extends EventReadingOffset

object SystemAcpiPowerStateEventOffset {

  case object S0G0Working extends SystemAcpiPowerStateEventOffset

  case object S1SleepingWithSystemHwProcessorContextMaintained extends SystemAcpiPowerStateEventOffset

  case object S2SleepingProcessorContextLost extends SystemAcpiPowerStateEventOffset

  case object S3SleepingProcessorAndHwContextLostMemoryRetained extends SystemAcpiPowerStateEventOffset

  case object S4NonvolatileSleepSuspendToDisk extends SystemAcpiPowerStateEventOffset

  case object S5G2SoftOff extends SystemAcpiPowerStateEventOffset

  case object S4S5SoftOffParticularS4S5StateCannotBeDetermined extends SystemAcpiPowerStateEventOffset

  case object G3MechanicalOff extends SystemAcpiPowerStateEventOffset

  case object SleepingInAnS1S2orS3State extends SystemAcpiPowerStateEventOffset

  case object G1SleepingS1S4StateCannotBeDetermined extends SystemAcpiPowerStateEventOffset

  case object S5EnteredByOverride extends SystemAcpiPowerStateEventOffset

  case object LegacyOnState extends SystemAcpiPowerStateEventOffset

  case object LegacyOffState extends SystemAcpiPowerStateEventOffset

  case object Unknown extends SystemAcpiPowerStateEventOffset

  def offsetFor(bit: Int): Option[SystemAcpiPowerStateEventOffset] = bit match {
    case 0x00 => Some(S0G0Working)
    case 0x01 => Some(S1SleepingWithSystemHwProcessorContextMaintained)
    case 0x02 => Some(S2SleepingProcessorContextLost)
    case 0x03 => Some(S3SleepingProcessorAndHwContextLostMemoryRetained)
    case 0x04 => Some(S4NonvolatileSleepSuspendToDisk)
    case 0x05 => Some(S5G2SoftOff)
    case 0x06 => Some(S4S5SoftOffParticularS4S5StateCannotBeDetermined)
    case 0x07 => Some(G3MechanicalOff)
    case 0x08 => Some(SleepingInAnS1S2orS3State)
    case 0x09 => Some(G1SleepingS1S4StateCannotBeDetermined)
    case 0x0a => Some(S5EnteredByOverride)
    case 0x0b => Some(LegacyOnState)
    case 0x0c => Some(LegacyOffState)
    case 0x0e => Some(Unknown)
    case _    => None
  }
}
