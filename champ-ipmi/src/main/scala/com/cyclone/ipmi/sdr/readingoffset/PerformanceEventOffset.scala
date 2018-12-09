package com.cyclone.ipmi.sdr.readingoffset

sealed trait PerformanceEventOffset extends EventReadingOffset

object PerformanceEventOffset {

  case object PerformanceMet extends PerformanceEventOffset

  case object PerformanceLags extends PerformanceEventOffset

  def offsetFor(bit: Int): Option[PerformanceEventOffset] = bit match {
    case 0x00 => Some(PerformanceEventOffset.PerformanceMet)
    case 0x01 => Some(PerformanceEventOffset.PerformanceLags)
    case _    => None
  }
}
