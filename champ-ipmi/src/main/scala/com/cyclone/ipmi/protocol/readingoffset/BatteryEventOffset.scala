package com.cyclone.ipmi.protocol.readingoffset

sealed trait BatteryEventOffset extends EventReadingOffset

object BatteryEventOffset {

  case object BatteryLowPredictiveFailure extends BatteryEventOffset

  case object BatteryFailed extends BatteryEventOffset

  case object BatteryPresenceDetected extends BatteryEventOffset

  def offsetFor(bit: Int): Option[BatteryEventOffset] = bit match {
    case 0x00 => Some(BatteryLowPredictiveFailure)
    case 0x01 => Some(BatteryFailed)
    case 0x02 => Some(BatteryPresenceDetected)
    case _    => None
  }
}
