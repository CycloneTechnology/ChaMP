package com.cyclone.ipmi.protocol

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetChannelAuthenticationCapabilities
import com.cyclone.ipmi.protocol.packet.IpmiVersion
import com.cyclone.ipmi.protocol.packet.SessionId.RemoteConsoleSessionId
import com.cyclone.ipmi.protocol.security.AuthenticationTypes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.EitherT._
import scalaz.Scalaz._

/**
  * Implements the logic to negotiate a session
  */
trait SessionNegotiator {
  /**
    * Performs necessary exchanges to negotiate a session
    *
    * @return a session context to be used for future commands
    */
  def negotiateSession(
    versionRequirement: IpmiVersionRequirement,
    remoteConsoleSessionId: RemoteConsoleSessionId,
    credentials: IpmiCredentials,
    privilegeLevel: PrivilegeLevel,
    requester: Requester)
    (implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[(SessionContext, IpmiVersion)]]
}

trait SessionNegotiatorComponent {
  def sessionNegotiator: SessionNegotiator
}

trait DefaultSessionNegotiatorComponent extends SessionNegotiatorComponent {
  lazy val sessionNegotiator: SessionNegotiator = DefaultSessionNegotiator
}

/**
  * Hands off session negotiation to a [[SessionNegotiationProtocol]]
  */
object DefaultSessionNegotiator extends SessionNegotiator {
  def negotiateSession(
    versionRequirement: IpmiVersionRequirement,
    remoteConsoleSessionId: RemoteConsoleSessionId,
    credentials: IpmiCredentials,
    privilegeLevel: PrivilegeLevel,
    requester: Requester)
    (implicit timeoutContext: TimeoutContext): Future[IpmiErrorOr[(SessionContext, IpmiVersion)]] = {

    def requiredVersion(supportsV20: Boolean): IpmiErrorOr[IpmiVersion] = (versionRequirement, supportsV20) match {
      case (IpmiVersionRequirement.V15Only, _)            => IpmiVersion.V15.right
      case (IpmiVersionRequirement.V20IfSupported, true)  => IpmiVersion.V20.right
      case (IpmiVersionRequirement.V20IfSupported, false) => IpmiVersion.V15.right
      case (IpmiVersionRequirement.V20Only, true)         => IpmiVersion.V20.right
      case (IpmiVersionRequirement.V20Only, false)        => UnsupportedRequiredVersion(IpmiVersion.V20).left
    }

    def protocolFor(version: IpmiVersion, authenticationTypes: AuthenticationTypes) = {
      import SessionNegotiationProtocol._
      version match {
        case IpmiVersion.V15 => V15(remoteConsoleSessionId,
          credentials,
          privilegeLevel,
          authenticationTypes,
          requester)

        case IpmiVersion.V20 => V20(remoteConsoleSessionId,
          credentials,
          privilegeLevel,
          requester)
      }
    }

    val result = for {
      caps <- eitherT(requester.makeRequest(
        GetChannelAuthenticationCapabilities.Command(privilegeLevel), IpmiVersion.V15))
      version <- eitherT(requiredVersion(caps.supportsV20).point[Future])
      protocol <- eitherT(protocolFor(version, caps.authenticationTypes).right.point[Future])
      sessionContext <- eitherT(protocol.negotiateSession())
    } yield (sessionContext, version)

    result.run
  }
}


