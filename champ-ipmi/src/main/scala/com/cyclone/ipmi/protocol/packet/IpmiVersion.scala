package com.cyclone.ipmi.protocol.packet

sealed trait IpmiVersion

object IpmiVersion {

  case object V15 extends IpmiVersion

  case object V20 extends IpmiVersion

}
