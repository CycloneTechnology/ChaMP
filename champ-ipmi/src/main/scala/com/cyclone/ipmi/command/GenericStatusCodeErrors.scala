package com.cyclone.ipmi.command

import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._


object GenericStatusCodeErrors {

  case object NodeBusy extends StatusCodeError {
    val code = StatusCode(0xC0)
    val message: String =
      """Node Busy.
        |Command could not be processed because command processing
        |resources are temporarily unavailable.""".stripMargin
  }

  case object InvalidCommand extends StatusCodeError {
    val code = StatusCode(0xC1)
    val message = "Invalid Command. Used to indicate an unrecognized or unsupported command."
  }

  case object InvalidForLun extends StatusCodeError {
    val code = StatusCode(0xC2)
    val message = "Command invalid for given LUN."
  }

  case object Timeout extends StatusCodeError {
    val code = StatusCode(0xC3)
    val message = "Timeout while processing command. Response unavailable."
  }

  case object OutOfSpace extends StatusCodeError {
    val code = StatusCode(0xC4)
    val message: String =
      """Out of space.Command could not be completed because of a lack of storage
        |space required to execute the given command operation.""".stripMargin
  }

  case object ReservationCancelled extends StatusCodeError {
    val code = StatusCode(0xC5)
    val message = "Reservation Cancelled or Invalid Reservation ID."
  }

  case object RequestTruncated extends StatusCodeError {
    val code = StatusCode(0xC6)
    val message = "Request data truncated."
  }

  case object RequestInvalidLength extends StatusCodeError {
    val code = StatusCode(0xC7)
    val message = "Request data length invalid."
  }

  case object RequestFieldLengthExceeded extends StatusCodeError {
    val code = StatusCode(0xC8)
    val message = "Request data field length limit exceeded."
  }

  case object ParameterOutOfRange extends StatusCodeError {
    val code = StatusCode(0xC9)
    val message: String =
      """Parameter out of range. One or more parameters in the data field of the
        |Request are out of range. This is different from ‘ Invalid data field ’ (CCh) code in
        |that it indicates that the erroneous field (s) has a contiguous range of possible
        |values.""".stripMargin
  }

  case object TooManyBytesRequested extends StatusCodeError {
    val code = StatusCode(0xCA)
    val message =
      "Cannot return number of requested data bytes."
  }

  case object SensorNotPresent extends StatusCodeError {
    val code = StatusCode(0xCB)
    val message =
      "Requested Sensor, data, or record not present."
  }

  case object InvalidDataField extends StatusCodeError {
    val code = StatusCode(0xCC)
    val message =
      "Invalid data field in Request"
  }

  case object IllegalCommand extends StatusCodeError {
    val code = StatusCode(0xCD)
    val message =
      "Command illegal for specified sensor or record type."
  }

  case object NoResponse extends StatusCodeError {
    val code = StatusCode(0xCE)
    val message =
      "Command response could not be provided."
  }

  case object DuplicatedRequest extends StatusCodeError {
    val code = StatusCode(0xCF)
    val message: String =
      """Cannot execute duplicated request.This completion code is for devices which
        |cannot return the response that was returned for the original instance of the
        |request.Such devices should provide separate commands that allow the
        |completion status of the original request to be determined.An Event Receiver
        |does not use this completion code, but returns the 00 h completion code in the
        |response to(valid) duplicated requests.""".stripMargin
  }

  case object SDRRepositoryUpdating extends StatusCodeError {
    val code = StatusCode(0xD0)
    val message =
      "Command response could not be provided. SDR Repository in update mode."
  }

  case object FirmwareUpdating extends StatusCodeError {
    val code = StatusCode(0xD1)
    val message =
      "Command response could not be provided. Device in firmware update mode."
  }

  case object BMCInitializationInProgress extends StatusCodeError {
    val code = StatusCode(0xD2)
    val message: String =
      """Command response could not be provided.BMC initialization or initialization
        |agent in progress.""".stripMargin
  }

  case object DestinationUnavailable extends StatusCodeError {
    val code = StatusCode(0xD3)
    val message: String =
      """ Destination unavailable.Cannot deliver request to selected destination.E.g.this
        |code can be returned if a request message is targeted to SMS, but receive
        |message queue reception is disabled for the particular channel.""".stripMargin
  }

  case object InsufficientPrivilege extends StatusCodeError {
    val code = StatusCode(0xD4)
    val message: String =
      """Cannot execute command due to insufficient privilege level or other security based
        |restriction (e.g.disabled for ‘ firmware firewall ’).""".stripMargin
  }

  case object CommandNotSupported extends StatusCodeError {
    val code = StatusCode(0xD5)
    val message: String =
      """Cannot execute command.Command, or request parameter(s), not supported
        |in present state.""".stripMargin
  }


  case object SubFunctionUnavailable extends StatusCodeError {
    val code = StatusCode(0xD6)
    val message: String =
      """Cannot execute command. Parameter is illegal because command sub-function
        |has been disabled or is unavailable(e.g.disabled for ‘ firmware firewall ’).""".stripMargin
  }

  case object UnspecifiedError extends StatusCodeError {
    val code = StatusCode(0xFF)
    val message = "Unspecified error"
  }

  case class DeviceSpecific(code: StatusCode) extends StatusCodeError {
    def message = s"Device-specific (OEM) error: $code"
  }

  //noinspection ScalaStyle
  private val codeLookupMap = Map(
    NodeBusy.code -> NodeBusy,
    InvalidCommand.code -> InvalidCommand,
    InvalidForLun.code -> InvalidForLun,
    Timeout.code -> Timeout,
    OutOfSpace.code -> OutOfSpace,
    ReservationCancelled.code -> ReservationCancelled,
    RequestTruncated.code -> RequestTruncated,
    RequestInvalidLength.code -> RequestInvalidLength,
    RequestFieldLengthExceeded.code -> RequestFieldLengthExceeded,
    ParameterOutOfRange.code -> ParameterOutOfRange,
    TooManyBytesRequested.code -> TooManyBytesRequested,
    SensorNotPresent.code -> SensorNotPresent,
    InvalidDataField.code -> InvalidDataField,
    IllegalCommand.code -> IllegalCommand,
    NoResponse.code -> NoResponse,
    DuplicatedRequest.code -> DuplicatedRequest,
    SDRRepositoryUpdating.code -> SDRRepositoryUpdating,
    FirmwareUpdating.code -> FirmwareUpdating,
    BMCInitializationInProgress.code -> BMCInitializationInProgress,
    DestinationUnavailable.code -> DestinationUnavailable,
    InsufficientPrivilege.code -> InsufficientPrivilege,
    CommandNotSupported.code -> CommandNotSupported,
    SubFunctionUnavailable.code -> SubFunctionUnavailable,
    UnspecifiedError.code -> UnspecifiedError
  )

  val lookup: PartialFunction[StatusCode, StatusCodeError] =
    codeLookupMap.orElse[StatusCode, StatusCodeError] {
      case c if c.code.in(0x01 to 0x7e) => DeviceSpecific(c)
    }
}
  