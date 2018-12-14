package com.cyclone.ipmi.protocol.readingoffset

sealed trait PowerSupplyEventOffset extends EventReadingOffset

object PowerSupplyEventOffset {

  case object PresenceDetected extends PowerSupplyEventOffset

  case object PowerSupplyFailureDetected extends PowerSupplyEventOffset

  case object PredictiveFailure extends PowerSupplyEventOffset

  case object PowerSupplyInputLostAcDc extends PowerSupplyEventOffset

  case object PowerSupplyInputLostOrOutOfRange extends PowerSupplyEventOffset

  case object PowerSupplyInputOutOfRangeButPresent extends PowerSupplyEventOffset

  case object ConfigurationError extends PowerSupplyEventOffset

  case object PowerSupplyInactiveInStandbyState extends PowerSupplyEventOffset

  def offsetFor(bit: Int): Option[PowerSupplyEventOffset] = bit match {

    case 0x00 => Some(PresenceDetected)
    case 0x01 => Some(PowerSupplyFailureDetected)
    case 0x02 => Some(PredictiveFailure)
    case 0x03 => Some(PowerSupplyInputLostAcDc)
    case 0x04 => Some(PowerSupplyInputLostOrOutOfRange)
    case 0x05 => Some(PowerSupplyInputOutOfRangeButPresent)
    case 0x06 => Some(ConfigurationError)
    case 0x07 => Some(PowerSupplyInactiveInStandbyState)
    case _    => None
  }
}
