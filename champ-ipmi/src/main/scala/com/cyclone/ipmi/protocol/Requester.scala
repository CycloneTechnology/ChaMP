package com.cyclone.ipmi.protocol

import akka.actor.{ActorContext, ActorRef}
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.protocol.packet._

import scala.concurrent.Future

/**
  * Utility for making requests and receiving responses
  */
trait Requester {
  /**
    * Sends a command and returns either an error or a result.
    *
    * @param command        the command
    * @param timeoutContext the context for the request
    * @return the result
    */
  def makeRequest[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    command: Cmd,
    version: IpmiVersion,
    sessionContext: SessionContext = SessionContext.NoSession,
    targetAddress: DeviceAddress = DeviceAddress.BmcAddress)
    (implicit timeoutContext: TimeoutContext, codec: CommandResultCodec[Cmd, Res]): Future[IpmiErrorOr[Res]]
}

trait RequesterFactory {
  def requester(
    actorContext: ActorContext,
    hub: ActorRef,
    seqNoManager: ActorRef): Requester
}

trait RequesterFactoryComponent {
  def requesterFactory: RequesterFactory
}
