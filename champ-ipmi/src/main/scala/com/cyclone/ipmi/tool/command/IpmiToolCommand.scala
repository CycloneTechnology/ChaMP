package com.cyclone.ipmi.tool.command

// TODO relax serializability req
trait IpmiToolCommand extends Serializable {
  def description(): String
}
