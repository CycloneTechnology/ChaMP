package com.cyclone.ipmi.api

import java.net.InetAddress

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi._
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.util.MockeryComponent
import org.jmock.AbstractExpectations._
import org.jmock.Expectations

import scala.concurrent.{ExecutionContext, Future}

/**
  * Mocked [[IpmiClientComponent]]
  */
trait MockIpmiClientComponent extends IpmiClientComponent {
  self: MockeryComponent =>

  lazy val ipmiClient = mockery.mock(classOf[IpmiClient])

  def willGetConnectionFor(
    address: InetAddress, port: Int): Future[IpmiConnection] = {
    val futConnection = Future.successful(mockery.mock(classOf[IpmiConnection]))

    mockery.checking(new Expectations {
      oneOf(ipmiClient).connectionFor(address, port)
      will(returnValue(futConnection))
    })

    futConnection
  }

  def willNegotiateSession(
    ipmiConnection: IpmiConnection,
    ipmiCredentials: IpmiCredentials,
    versionRequirement: IpmiVersionRequirement,
    privilegeLevel: PrivilegeLevel = PrivilegeLevel.User,
    result: IpmiErrorOr[Unit])
    (implicit timeoutContext: TimeoutContext): Unit =
    mockery.checking(new Expectations {
      oneOf(ipmiConnection).negotiateSession(
        ipmiCredentials,
        versionRequirement,
        privilegeLevel
      )
      will(returnValue(Future.successful(result)))
    })


  def willElevatePrivilegeLevel(
    ipmiConnection: IpmiConnection,
    privilegeLevel: PrivilegeLevel = PrivilegeLevel.User,
    result: IpmiErrorOr[Option[PrivilegeLevel]])
    (implicit ec: ExecutionContext, timeoutContext: TimeoutContext): Unit =
    mockery.checking(new Expectations {
      oneOf(ipmiConnection).elevatePrivilegeLevel(
        privilegeLevel
      )
      will(returnValue(Future.successful(result)))
    })

  def willExecuteCommand[Cmd <: IpmiStandardCommand, Res <: IpmiCommandResult](
    ipmiConnection: IpmiConnection,
    command: Cmd,
    result: IpmiErrorOr[Res])
    (implicit timeoutContext: TimeoutContext, codec: CommandResultCodec[Cmd, Res]): Unit =
    mockery.checking(new Expectations {
      oneOf(ipmiConnection).executeCommandOrError(command)
      will(returnValue(Future.successful(result)))
    })

  def willClosedown(ipmiConnection: IpmiConnection): Unit =
    mockery.checking(new Expectations {
      oneOf(ipmiConnection).closedown()
      will(returnValue(Future.successful(())))
    })


}
