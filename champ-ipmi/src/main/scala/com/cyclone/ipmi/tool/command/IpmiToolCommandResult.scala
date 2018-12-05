package com.cyclone.ipmi.tool.command

/**
  * The result of running a command
  */
trait IpmiToolCommandResult {
  /**
    * Result used for tabulation
    */
  def tabulationSource: AnyRef = this
}

object IpmiToolCommandResult
