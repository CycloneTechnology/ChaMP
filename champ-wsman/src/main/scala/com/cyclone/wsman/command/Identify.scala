package com.cyclone.wsman.command

import com.cyclone.wsman.command.WSManCommands._
import com.cyclone.wsman.impl.WSManOperations
import com.cyclone.wsman.{WSManError, WSManOperationContext}
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object Identify extends WSManCommand {

  implicit object Executor extends CommandExecutor[Identify.type, WSManIdentifyResult] {
    def execute(command: Identify.type)(implicit context: WSManOperationContext): Future[\/[WSManError, WSManIdentifyResult]] =
      WSManOperations.identify(context.operationDeadline).map(_.map(_.external))
  }

}

