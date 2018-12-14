package com.cyclone.ipmi.protocol.readingoffset

sealed trait PowerUnitEventOffset extends EventReadingOffset

object PowerUnitEventOffset {

  case object PowerOffPowerDown extends PowerUnitEventOffset

  case object PowerCycle extends PowerUnitEventOffset

  case object PowerDown240VA extends PowerUnitEventOffset

  case object InterlockPowerDown extends PowerUnitEventOffset

  case object ACLostPowerInputLostThePowerSourceForThePowerUnitWasLost extends PowerUnitEventOffset

  case object SoftPowerControlFailureUnitDidNotRespondToRequestToTurnOn extends PowerUnitEventOffset

  case object PowerUnitFailureDetected extends PowerUnitEventOffset

  case object PredictiveFailure extends PowerUnitEventOffset

  def offsetFor(bit: Int): Option[PowerUnitEventOffset] = bit match {
    case 0x00 => Some(PowerOffPowerDown)
    case 0x01 => Some(PowerCycle)
    case 0x02 => Some(PowerDown240VA)
    case 0x03 => Some(InterlockPowerDown)
    case 0x04 => Some(ACLostPowerInputLostThePowerSourceForThePowerUnitWasLost)
    case 0x05 => Some(SoftPowerControlFailureUnitDidNotRespondToRequestToTurnOn)
    case 0x06 => Some(PowerUnitFailureDetected)
    case 0x07 => Some(PredictiveFailure)
    case _    => None
  }
}
