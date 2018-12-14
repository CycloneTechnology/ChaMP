package com.cyclone.ipmi.protocol.readingoffset

sealed trait ManagementSubsystemHealthEventOffset extends EventReadingOffset

object ManagementSubsystemHealthEventOffset {

  case object SensorAccessDegradedOrUnavailable extends ManagementSubsystemHealthEventOffset

  case object ControllerAccessDegradedOrUnavailable extends ManagementSubsystemHealthEventOffset

  case object ManagementControllerOffline extends ManagementSubsystemHealthEventOffset

  case object ManagementControllerUnavailable extends ManagementSubsystemHealthEventOffset

  case object SensorFailure extends ManagementSubsystemHealthEventOffset

  case object FruFailure extends ManagementSubsystemHealthEventOffset

  def offsetFor(bit: Int): Option[ManagementSubsystemHealthEventOffset] = bit match {
    case 0x00 => Some(SensorAccessDegradedOrUnavailable)
    case 0x01 => Some(ControllerAccessDegradedOrUnavailable)
    case 0x02 => Some(ManagementControllerOffline)
    case 0x03 => Some(ManagementControllerUnavailable)
    case 0x04 => Some(SensorFailure)
    case 0x05 => Some(FruFailure)
    case _    => None
  }
}
