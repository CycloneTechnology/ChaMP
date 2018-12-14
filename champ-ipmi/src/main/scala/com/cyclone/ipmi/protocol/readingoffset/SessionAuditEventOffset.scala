package com.cyclone.ipmi.protocol.readingoffset

sealed trait SessionAuditEventOffset extends EventReadingOffset

object SessionAuditEventOffset {

  case object SessionActivated extends SessionAuditEventOffset

  case object SessionDeactivated extends SessionAuditEventOffset

  case object InvalidUsernameOrPassword extends SessionAuditEventOffset

  case object InvalidPasswordDisable extends SessionAuditEventOffset

  def offsetFor(bit: Int): Option[SessionAuditEventOffset] = bit match {
    case 0x00 => Some(SessionActivated)
    case 0x01 => Some(SessionDeactivated)
    case 0x02 => Some(InvalidUsernameOrPassword)
    case 0x03 => Some(InvalidPasswordDisable)
    case _    => None
  }
}
