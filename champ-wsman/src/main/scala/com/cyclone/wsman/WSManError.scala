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
    val faultNode: NodeSeq = response \ "Body" \ "Fault"

    WSManQueryError(
      message = message(faultNode),
      reason = reason(faultNode),
      code = code(faultNode),
      subCode = subCode(faultNode),
      detailFaultMessage = detailFaultMessage(faultNode),
      detailProviderFaultMessage = detailProviderFaultMessage(faultNode),
      faultDetail = faultDetail(faultNode)
    )
  }

  private def optionalText(nodes: NodeSeq) =
    nodes match {
      case NodeSeq.Empty => None
      case ns: NodeSeq   => Some(ns.text)
    }

  private def code(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Code" \ "Value")

  private def subCode(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Code" \ "Subcode" \ "Value")

  private def reason(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Reason" \ "Text")

  private def detailFaultMessage(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Detail" \ "WSManFault" \ "Message")

  private def faultDetail(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Detail" \ "FaultDetail")

  private def message(faultNode: NodeSeq): String =
    reason(faultNode)
      .getOrElse("Unknown") + "\nDetail: " +
    detailProviderFaultMessage(faultNode).orElse(detailFaultMessage(faultNode)).getOrElse("Unknown")

  // This will pick up on ProviderFault\MSFT_WmiError\Message used sometimes by Microsoft.
  // (May fail for other providers so will fallback to std detail fault message.)
  private def detailProviderFaultMessage(faultNode: NodeSeq): Option[String] =
    optionalText(faultNode \ "Detail" \ "WSManFault" \ "Message" \ "ProviderFault" \\ "Message")
}

case object RequestTimeout extends WSManError {
  val message = "Request timeout"
}

case object DeadlineReached extends WSManError {
  val message = "Timeout"
}
