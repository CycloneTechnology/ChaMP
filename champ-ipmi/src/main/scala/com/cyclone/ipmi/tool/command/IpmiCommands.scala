package com.cyclone.ipmi.tool.command

import com.cyclone.command.Commands
import com.cyclone.ipmi.{IpmiError, IpmiOperationContext}

case object IpmiCommands extends Commands {
  type Ctx = IpmiOperationContext
  type Command = IpmiToolCommand
  type Result = IpmiToolCommandResult
  type Err = IpmiError

}
