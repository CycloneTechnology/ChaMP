package com.cyclone.ipmi.protocol.readingoffset

sealed trait ButtonSwitchEventOffset extends EventReadingOffset

object ButtonSwitchEventOffset {

  case object PowerButtonPressed extends ButtonSwitchEventOffset

  case object SleepButtonPressed extends ButtonSwitchEventOffset

  case object ResetButtonPressed extends ButtonSwitchEventOffset

  case object FruLatchOpen extends ButtonSwitchEventOffset

  case object FruServiceRequestButton extends ButtonSwitchEventOffset

  def offsetFor(bit: Int): Option[ButtonSwitchEventOffset] = bit match {
    case 0x00 => Some(PowerButtonPressed)
    case 0x01 => Some(SleepButtonPressed)
    case 0x02 => Some(ResetButtonPressed)
    case 0x03 => Some(FruLatchOpen)
    case 0x04 => Some(FruServiceRequestButton)
    case _    => None
  }
}
