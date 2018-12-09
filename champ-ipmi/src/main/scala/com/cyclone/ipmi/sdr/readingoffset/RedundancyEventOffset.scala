package com.cyclone.ipmi.sdr.readingoffset

sealed trait RedundancyEventOffset extends EventReadingOffset

object RedundancyEventOffset {

  case object FullyRedundant extends RedundancyEventOffset

  case object RedundancyLost extends RedundancyEventOffset

  case object RedundancyDegraded extends RedundancyEventOffset

  case object NonRedundantSufficientResourcesFromRedundant extends RedundancyEventOffset

  case object NonRedundantSufficientResourcesFromInsufficientResources extends RedundancyEventOffset

  case object NonRedundantInsufficientResources extends RedundancyEventOffset

  case object RedundancyDegradedFromFullyRedundant extends RedundancyEventOffset

  case object RedundancyDegradedFromNonRedundant extends RedundancyEventOffset

  def offsetFor(bit: Int): Option[RedundancyEventOffset] = bit match {
    case 0x00 => Some(RedundancyEventOffset.FullyRedundant)
    case 0x01 => Some(RedundancyEventOffset.RedundancyLost)
    case 0x02 => Some(RedundancyEventOffset.RedundancyDegraded)
    case 0x03 => Some(RedundancyEventOffset.NonRedundantSufficientResourcesFromRedundant)
    case 0x04 =>
      Some(RedundancyEventOffset.NonRedundantSufficientResourcesFromInsufficientResources)
    case 0x05 => Some(RedundancyEventOffset.NonRedundantInsufficientResources)
    case 0x06 => Some(RedundancyEventOffset.RedundancyDegradedFromFullyRedundant)
    case 0x07 => Some(RedundancyEventOffset.RedundancyDegradedFromNonRedundant)
    case _    => None
  }
}
