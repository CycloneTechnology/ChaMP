package com.cyclone.ipmi

import com.cyclone.ipmi.command.StatusCode
import com.cyclone.ipmi.protocol.packet.{IpmiVersion, PayloadType}
import com.cyclone.ipmi.protocol.security.AuthenticationTypes
import scalaz.{\/, EitherT}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NoStackTrace

/**
  * An error related to IPMI.
  */
trait IpmiError {
  def message: String

  /**
    * @return set if whether the command may be retried after a small duration has elapsed
    */
  def retryAfter: Option[FiniteDuration] = None
}

object IpmiError {
  type IpmiErrorOr[+x] = \/[IpmiError, x]
  type FutureIpmiErrorOr[x] = EitherT[Future, IpmiError, x]

  type StatusCodeErrorOr[+x] = \/[StatusCodeError, x]

  def toThrowable(err: IpmiError): Throwable =
    err match {
      case IpmiExceptionError(ex) => ex
      case _                      => IpmiErrorException(err)
    }
}

/**
  * An exception wrapping an error
  */
case class IpmiErrorException(err: IpmiError) extends Exception with NoStackTrace{
  override def toString: String = s"IpmiErrorException($err)"
}

/**
  * An error from an exception
  */
case class IpmiExceptionError(e: Throwable) extends IpmiError {
  def message: String = e.getLocalizedMessage
}

/**
  * An error related to decoding a received IPMI message.
  */
case class IpmiDecodeError(message: String) extends IpmiError

/**
  * An error related to a failed command
  */
trait StatusCodeError extends IpmiError {
  def code: StatusCode
}

case class SimpleStatusCodeError(
  code: StatusCode,
  message: String,
  override val retryAfter: Option[FiniteDuration] = None
) extends StatusCodeError

case class IntegrityCheckError(message: String) extends IpmiError

case class UnknownStatusCodeError(code: StatusCode) extends StatusCodeError {
  val message = s"Unknown error: $code"
}

case class UnsupportedRequiredVersion(version: IpmiVersion) extends IpmiError {
  val message = s"Version $version not supported by device"
}

case class UnsupportedPayloadType(payloadType: PayloadType) extends IpmiError {
  val message = s"Unsupported payload type $payloadType"
}

case class NoSupportedAuthenticationTypes(authenticationTypes: AuthenticationTypes) extends IpmiError {

  val message =
    s"None of the device's authentication capabilities: ${authenticationTypes.types.mkString(", ")} is supported"
}

case object NoSupportedCipherSuites extends IpmiError {
  val message = s"None of the device's authentication capabilities is supported"
}

case object DeadlineReached extends IpmiError {
  val message = "Timeout"
}

case object TimeoutTooManyAttempts extends IpmiError {
  val message = "A request timed out after too many failed attempts"
}
