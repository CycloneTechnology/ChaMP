package com.cyclone.ipmi.protocol.packet

import com.cyclone.ipmi.command.CommandCode

trait IpmiCommand

trait IpmiStandardCommand extends IpmiCommand {
  def networkFunction: NetworkFunction

  def commandCode: CommandCode

  val channelNumber: Byte = 0x0E.toByte
}

trait IpmiSessionActivationCommand extends IpmiCommand {
  def payloadType: PayloadType
}

trait IpmiCommandResult
