package com.cyclone.ipmi.sdr.readingoffset

sealed trait PhysicalSecurityEventOffset extends EventReadingOffset

object PhysicalSecurityEventOffset {

  case object GeneralChassisIntrusion extends PhysicalSecurityEventOffset

  case object DriveBayIntrusion extends PhysicalSecurityEventOffset

  case object IOCardAreaIntrusion extends PhysicalSecurityEventOffset

  case object ProcessorAreaIntrusion extends PhysicalSecurityEventOffset

  case object LanLeashLost extends PhysicalSecurityEventOffset

  case object UnauthorizedDock extends PhysicalSecurityEventOffset

  case object FanAreaIntrusion extends PhysicalSecurityEventOffset

  def offsetFor(bit: Int): Option[PhysicalSecurityEventOffset] = bit match {
    case 0x00 => Some(GeneralChassisIntrusion)
    case 0x01 => Some(DriveBayIntrusion)
    case 0x02 => Some(IOCardAreaIntrusion)
    case 0x03 => Some(ProcessorAreaIntrusion)
    case 0x04 => Some(LanLeashLost)
    case 0x05 => Some(UnauthorizedDock)
    case 0x06 => Some(FanAreaIntrusion)
    case _    => None
  }
}
