package com.cyclone.ipmi.protocol.packet

import akka.util.ByteString
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec.{Coder, _}
import com.cyclone.ipmi.protocol.SessionContext
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.security.AuthenticationType

object IpmiSessionWrapper {

  def encode(wrapper: IpmiSessionWrapperRequest): ByteString =
    wrapper match {
      case wrapper: Ipmi20SessionWrapper.Request =>
        Ipmi20SessionWrapper.Request.coder.encode(wrapper)
      case wrapper: Ipmi15SessionWrapper.Request =>
        Ipmi15SessionWrapper.Request.coder.encode(wrapper)
    }

  def decode(
    data: ByteString,
    sessionContext: SessionContext
  ): IpmiErrorOr[IpmiSessionWrapperResponse] = {
    // Version is determined based on authentication type...
    val decoder = data(0).as[AuthenticationType] match {
      case AuthenticationType.RmcpPlus => Ipmi20SessionWrapper.RawResponse.decoder.handleExceptions
      case _                           => Ipmi15SessionWrapper.RawResponse.decoder.handleExceptions
    }

    for {
      rawResponse <- decoder.decode(data)
      response    <- rawResponse.authenticatedResponse(sessionContext)
    } yield response
  }

  def wrapperFor[P <: IpmiRequestPayload: Coder](
    payload: P,
    version: IpmiVersion,
    sessionSequenceNumber: SessionSequenceNumber,
    sessionContext: SessionContext
  ): IpmiSessionWrapperRequest = {

    version match {
      case IpmiVersion.V20 =>
        Ipmi20SessionWrapper.Request.fromContext(
          payload,
          sessionSequenceNumber,
          sessionContext
        )

      case IpmiVersion.V15 =>
        Ipmi15SessionWrapper.Request.fromContext(
          payload,
          sessionSequenceNumber,
          sessionContext
        )
    }
  }
}

/**
  * An IPMI message
  */
trait IpmiSessionWrapper {
  def managedSystemSessionId: ManagedSystemSessionId

  def sessionSequenceNumber: SessionSequenceNumber

  def payloadType: PayloadType

  def payload: ByteString
}

trait IpmiSessionWrapperRequest extends IpmiSessionWrapper

trait IpmiSessionWrapperResponse extends IpmiSessionWrapper

/**
  * A raw response that has not been checked for authenticity & integrity
  */
trait IpmiSessionWrapperRawResponse {

  /**
    * @return the authenticated response or an error
    */
  def authenticatedResponse(sessionContext: SessionContext): IpmiErrorOr[IpmiSessionWrapperResponse]
}
