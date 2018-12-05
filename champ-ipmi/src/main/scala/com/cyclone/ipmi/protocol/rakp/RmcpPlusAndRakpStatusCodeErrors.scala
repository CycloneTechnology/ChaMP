package com.cyclone.ipmi.protocol.rakp

import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.command.StatusCode

/**
  * RMCP+ and RAKP Message Status Codes
  */
object RmcpPlusAndRakpStatusCodeErrors {

  case object InsufficientResources extends StatusCodeError {
    val code = StatusCode(0x01)
    val message = "Insufficient resources to create a session"
  }

  case object InvalidSessionId extends StatusCodeError {
    val code = StatusCode(0x02)
    val message = "Invalid Session ID"
  }

  case object InvalidPayloadType extends StatusCodeError {
    val code = StatusCode(0x03)
    val message = "Invalid payload type"
  }

  case object InvalidAuthenticationAlgorithm extends StatusCodeError {
    val code = StatusCode(0x04)
    val message = "Invalid authentication algorithm"
  }

  case object InvalidIntegrityAlgorithm extends StatusCodeError {
    val code = StatusCode(0x05)
    val message = "Invalid integrity algorithm"
  }

  case object NoMatchingAuthenticationPayload extends StatusCodeError {
    val code = StatusCode(0x06)
    val message = "No matching authentication payload"
  }

  case object NoMatchingIntegrityPayload extends StatusCodeError {
    val code = StatusCode(0x07)
    val message = "No matching integrity payload"
  }

  case object InactiveSessionId extends StatusCodeError {
    val code = StatusCode(0x08)
    val message = "Inactive Session ID"
  }

  case object InvalidRole extends StatusCodeError {
    val code = StatusCode(0x09)
    val message = "Invalid role"
  }

  case object UnauthorizedRole extends StatusCodeError {
    val code = StatusCode(0x0A)
    val message = "Unauthorized role or privilege level requested"
  }

  case object InsufficientResourcesForRole extends StatusCodeError {
    val code = StatusCode(0x0B)
    val message = "Insufficient resources to create a session at the requested role"
  }

  case object InvalidNameLength extends StatusCodeError {
    val code = StatusCode(0x0C)
    val message = "Invalid name length"
  }

  case object UnauthorizedName extends StatusCodeError {
    val code = StatusCode(0x0D)
    val message = "Unauthorized name"
  }

  case object UnauthorizedGUID extends StatusCodeError {
    val code = StatusCode(0x0E)

    val message: String =
      """Unauthorized GUID.
        |(GUID that BMC submitted in RAKP Message 2 was not
        |accepted by remote console)""".stripMargin
  }

  case object InvalidIntegrityCheckValue extends StatusCodeError {
    val code = StatusCode(0x0F)
    val message = "Invalid integrity check value"
  }

  case object InvalidConfidentialityAlgorithm extends StatusCodeError {
    val code = StatusCode(0x10)
    val message = "Invalid confidentiality algorithm"
  }

  case object NoMatchingCipherSuite extends StatusCodeError {
    val code = StatusCode(0x11)
    val message = "No Cipher Suite match with proposed security algorithms"
  }

  case object IllegalParameter extends StatusCodeError {
    val code = StatusCode(0x12)
    val message = "Illegal or unrecognized parameter"
  }

  //noinspection ScalaStyle
  private val codeLookupMap = Map(
    InsufficientResources.code           -> InsufficientResources,
    InvalidSessionId.code                -> InvalidSessionId,
    InvalidPayloadType.code              -> InvalidPayloadType,
    InvalidAuthenticationAlgorithm.code  -> InvalidAuthenticationAlgorithm,
    InvalidIntegrityAlgorithm.code       -> InvalidIntegrityAlgorithm,
    NoMatchingAuthenticationPayload.code -> NoMatchingAuthenticationPayload,
    NoMatchingIntegrityPayload.code      -> NoMatchingIntegrityPayload,
    InactiveSessionId.code               -> InactiveSessionId,
    InvalidRole.code                     -> InvalidRole,
    UnauthorizedRole.code                -> UnauthorizedRole,
    InsufficientResourcesForRole.code    -> InsufficientResourcesForRole,
    InvalidNameLength.code               -> InvalidNameLength,
    UnauthorizedName.code                -> UnauthorizedName,
    UnauthorizedGUID.code                -> UnauthorizedGUID,
    InvalidIntegrityCheckValue.code      -> InvalidIntegrityCheckValue,
    InvalidConfidentialityAlgorithm.code -> InvalidConfidentialityAlgorithm,
    NoMatchingCipherSuite.code           -> NoMatchingCipherSuite,
    IllegalParameter.code                -> IllegalParameter
  )

  val lookup: PartialFunction[StatusCode, StatusCodeError] = codeLookupMap
}
