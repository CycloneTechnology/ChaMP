package com.cyclone.ipmi

import java.net.InetAddress

import com.google.common.net.InetAddresses

/**
  * Represents a target for an Ipmi command
  */
trait IpmiTarget

object IpmiTarget {

  val defaultPort = 623

  /**
    * For IPMI over LAN
    */
  case class LAN(
    inetAddress: InetAddress,
    port: Int = IpmiTarget.defaultPort,
    credentials: IpmiCredentials,
    privilegeLevel: PrivilegeLevel = PrivilegeLevel.User,
    versionRequirement: IpmiVersionRequirement = IpmiVersionRequirement.V20IfSupported
  ) extends IpmiTarget

  object LAN {
    def forHost(
      host: String,
      port: Int = IpmiTarget.defaultPort,
      credentials: IpmiCredentials,
      privilegeLevel: PrivilegeLevel = PrivilegeLevel.User,
      versionRequirement: IpmiVersionRequirement = IpmiVersionRequirement.V20IfSupported): LAN =
      LAN(InetAddresses.forString(host), port, credentials, privilegeLevel, versionRequirement)
  }

}
