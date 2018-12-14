package com.cyclone.ipmi.protocol.readingoffset

sealed trait CableInterconnectEventOffset extends EventReadingOffset

object CableInterconnectEventOffset {

  case object CableInterconnectIsConnected extends CableInterconnectEventOffset

  case object ConfigurationErrorIncorrectCableConnectedIncorrectInterconnection extends CableInterconnectEventOffset

  def offsetFor(bit: Int): Option[CableInterconnectEventOffset] = bit match {
    case 0x00 => Some(CableInterconnectIsConnected)
    case 0x01 => Some(ConfigurationErrorIncorrectCableConnectedIncorrectInterconnection)
    case _    => None
  }
}
