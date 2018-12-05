package com.cyclone.wsman.command

import com.cyclone.command.Commands
import com.cyclone.wsman.{WSManError, WSManOperationContext}

case object WSManCommands extends Commands {
  type Ctx = WSManOperationContext
  type Command = WSManCommand
  type Result = WSManCommandResult
  type Err = WSManError
}
