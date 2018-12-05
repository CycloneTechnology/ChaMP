package com.cyclone.wsman

import com.cyclone.wsman.impl.WSManAvailability
import scalaz.{\/, EitherT}

import scala.concurrent.Future
import scala.util.control.NoStackTrace
import scala.xml.{Node, NodeSeq}

/**
  * An error related to WSMan
  */
trait WSManError {
  def message: String
}

object WSManError {
  type WSManErrorOr[+x] = \/[WSManError, x]
  type FutureWSManErrorOr[x] = EitherT[Future, WSManError, x]

  def toThrowable(err: WSManError): Throwable =
    err match {
      case WSManExceptionError(ex) => ex
      case _                       => WSManErrorException(err)
    }
}

/**
  * An exception wrapping an error
  */
case class WSManErrorException(err: WSManError) extends Exception with NoStackTrace

/**
  * An error from an exception
  */
case class WSManExceptionError(e: Throwable) extends WSManError {
  def message: String = e.getLocalizedMessage
}

case class WSManAvailabilityTestError(availability: WSManAvailability) extends WSManError {
  def message: String = availability.message
}

/**
  * An error from IO-related exception
  */
case class WSManIOError(reason: Option[String], cause: Option[Throwable]) extends WSManError {

  def message: String =
    "IO error:" + reason.getOrElse("No further info")
}

case class WSManAuthenticationError(reason: Option[String], cause: Option[Throwable]) extends WSManError {

  def message: String =
    "Authentication error:" + reason.getOrElse("No further info")
}

/**
  * For certain HTTP 400 codes where there is no response XML
  */
case class WSManRequestError(message: String) extends WSManError

case class WSManQueryError(
  message: String,
  reason: Option[String],
  code: Option[String],
  subCode: Option[String],
  detailFaultMessage: Option[String],
  detailProviderFaultMessage: Option[String],
  faultDetail: Option[String]
) extends WSManError

object WSManQueryError {

  def apply(response: Node): WSManQueryError = {

    lazy val faultNode = response \ "Body" \ "Fault"

    def message: String =
      reason
        .getOrElse("Unknown") + "\nDetail: " +
      detailProviderFaultMessage.orElse(detailFaultMessage).getOrElse("Unknown")

    def code: Option[String] =
      optionalText(faultNode \ "Code" \ "Value")

    def subCode: Option[String] =
      optionalText(faultNode \ "Code" \ "Subcode" \ "Value")

    def reason: Option[String] =
      optionalText(faultNode \ "Reason" \ "Text")

    def detailFaultMessage: Option[String] =
      optionalText(faultNode \ "Detail" \ "WSManFault" \ "Message")

    def faultDetail: Option[String] =
      optionalText(faultNode \ "Detail" \ "FaultDetail")

    // This will pick up on ProviderFault\MSFT_WmiError\Message used sometimes by Microsoft.
    // (May fail for other providers so will fallback to std detail fault message.)
    def detailProviderFaultMessage: Option[String] =
      optionalText(faultNode \ "Detail" \ "WSManFault" \ "Message" \ "ProviderFault" \\ "Message")

    def optionalText(nodes: NodeSeq) =
      nodes match {
        case NodeSeq.Empty => None
        case ns: NodeSeq   => Some(ns.text)
      }

    WSManQueryError(
      message = message,
      reason = reason,
      code = code,
      subCode = subCode,
      detailFaultMessage = detailFaultMessage,
      detailProviderFaultMessage = detailProviderFaultMessage,
      faultDetail = faultDetail
    )
  }
}

case object RequestTimeout extends WSManError {
  val message = "Request timeout"
}

case object DeadlineReached extends WSManError {
  val message = "Timeout"
}
