package com.cyclone.ipmi.protocol.readingoffset

sealed trait PlatformSecurityEventOffset extends EventReadingOffset

object PlatformSecurityEventOffset {

  case object SecureMode extends PlatformSecurityEventOffset

  case object PreBootPasswordViolationUserPassword extends PlatformSecurityEventOffset

  case object PreBootPasswordViolationAttemptSetupPassword extends PlatformSecurityEventOffset

  case object PreBootPasswordViolationNetworkBootPassword extends PlatformSecurityEventOffset

  case object OtherPreBootPasswordViolation extends PlatformSecurityEventOffset

  case object OutOfBandAccessPasswordViolation extends PlatformSecurityEventOffset

  def offsetFor(bit: Int): Option[PlatformSecurityEventOffset] = bit match {
    case 0x00 => Some(SecureMode)
    case 0x01 => Some(PreBootPasswordViolationUserPassword)
    case 0x02 => Some(PreBootPasswordViolationAttemptSetupPassword)
    case 0x03 => Some(PreBootPasswordViolationNetworkBootPassword)
    case 0x04 => Some(OtherPreBootPasswordViolation)
    case 0x05 => Some(OutOfBandAccessPasswordViolation)
    case _    => None
  }
}
