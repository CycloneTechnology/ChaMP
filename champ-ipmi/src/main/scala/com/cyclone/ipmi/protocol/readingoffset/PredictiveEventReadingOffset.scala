package com.cyclone.ipmi.protocol.readingoffset

sealed trait PredictiveEventOffset extends EventReadingOffset

object PredictiveEventOffset {

  case object PredictiveFailureDeasserted extends PredictiveEventOffset

  case object PredictiveFailureAsserted extends PredictiveEventOffset

  def offsetFor(bit: Int): Option[PredictiveEventOffset] = bit match {
    case 0x00 => Some(PredictiveEventOffset.PredictiveFailureDeasserted)
    case 0x01 => Some(PredictiveEventOffset.PredictiveFailureAsserted)
    case _    => None
  }
}
