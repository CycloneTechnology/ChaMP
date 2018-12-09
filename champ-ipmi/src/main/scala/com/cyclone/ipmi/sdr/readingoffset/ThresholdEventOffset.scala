package com.cyclone.ipmi.sdr.readingoffset

sealed trait ThresholdEventOffset extends EventReadingOffset

object ThresholdEventOffset {

  case object LowerNonCriticalGoingLow extends ThresholdEventOffset

  case object LowerNonCriticalGoingHigh extends ThresholdEventOffset

  case object LowerCriticalGoingLow extends ThresholdEventOffset

  case object LowerCriticalGoingHigh extends ThresholdEventOffset

  case object LowerNonRecoverableGoingLow extends ThresholdEventOffset

  case object LowerNonRecoverableGoingHigh extends ThresholdEventOffset

  case object UpperNonCriticalGoingLow extends ThresholdEventOffset

  case object UpperNonCriticalGoingHigh extends ThresholdEventOffset

  case object UpperCriticalGoingLow extends ThresholdEventOffset

  case object UpperCriticalGoingHigh extends ThresholdEventOffset

  case object UpperNonRecoverableGoingLow extends ThresholdEventOffset

  case object UpperNonRecoverableGoingHigh extends ThresholdEventOffset

  def offsetFor(bit: Int): Option[ThresholdEventOffset] = bit match {
    case 0x00 => Some(ThresholdEventOffset.LowerNonCriticalGoingLow)
    case 0x01 => Some(ThresholdEventOffset.LowerNonCriticalGoingHigh)
    case 0x02 => Some(ThresholdEventOffset.LowerCriticalGoingLow)
    case 0x03 => Some(ThresholdEventOffset.LowerCriticalGoingHigh)
    case 0x04 => Some(ThresholdEventOffset.LowerNonRecoverableGoingLow)
    case 0x05 => Some(ThresholdEventOffset.LowerNonRecoverableGoingHigh)
    case 0x06 => Some(ThresholdEventOffset.UpperNonCriticalGoingLow)
    case 0x07 => Some(ThresholdEventOffset.UpperNonCriticalGoingHigh)
    case 0x08 => Some(ThresholdEventOffset.UpperCriticalGoingLow)
    case 0x09 => Some(ThresholdEventOffset.UpperCriticalGoingHigh)
    case 0x0A => Some(ThresholdEventOffset.UpperNonRecoverableGoingLow)
    case 0x0B => Some(ThresholdEventOffset.UpperNonRecoverableGoingHigh)
    case _    => None
  }
}
