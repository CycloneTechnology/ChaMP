package com.cyclone.ipmi.protocol

import akka.actor.{ActorContext, ActorRef}
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.protocol.packet._
import com.cyclone.util.MockeryComponent
import org.jmock.AbstractExpectations._
import org.jmock.Expectations

import scala.concurrent.Future

/**
  * Mocked [[RequesterFactoryComponent]] that uses a fixed mock [[Requester]]
  */
trait MockRequesterFactoryComponent extends RequesterFactoryComponent {
  self: MockeryComponent =>

  val requester = mockery.mock(classOf[Requester])

  def requesterFactory = new RequesterFactory {
    def requester(actorContext: ActorContext, hub: ActorRef, seqNoManager: ActorRef) = self.requester
  }

  def willMakeRequest[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    command: Cmd,
    version: IpmiVersion,
    sessionContext: SessionContext,
    result: Future[IpmiErrorOr[Res]],
    targetAddress: DeviceAddress = DeviceAddress.BmcAddress
  )(implicit timeoutContext: TimeoutContext, codec: CommandResultCodec[Cmd, Res]) =
    mockery.checking(new Expectations {
      oneOf(requester).makeRequest(command, version, sessionContext, targetAddress)
      will(returnValue(result))
    })

  def willMakeRequestWithContext[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
    command: Cmd,
    timeoutContext: TimeoutContext,
    version: IpmiVersion,
    sessionContext: SessionContext,
    result: Future[IpmiErrorOr[Res]]
  )(implicit codec: CommandResultCodec[Cmd, Res]): Unit =
    willMakeRequest(command, version, sessionContext, result)(timeoutContext, codec)
}
