package com.cyclone.ipmi.command

import com.cyclone.ipmi.protocol.packet.IpmiCommandResult
import com.cyclone.ipmi.{StatusCodeError, UnknownStatusCodeError}

import scala.language.postfixOps

/**
  * Knows how to lookup a [[StatusCode]] for a particular type of command result to get an error message
  */
case class StatusCodeTranslator[-Res <: IpmiCommandResult](
  specificLookup: PartialFunction[StatusCode, StatusCodeError] = PartialFunction.empty
) {

  /**
    * @return the error for the status code if it is an error condition
    */
  final def lookupStatusCode(statusCode: StatusCode): Option[StatusCodeError] = {
    if (statusCode.code == 0.toByte) None
    else {
      specificLookup
        .orElse[StatusCode, StatusCodeError](GenericStatusCodeErrors.lookup)
        .orElse[StatusCode, StatusCodeError] { case x: StatusCode => UnknownStatusCodeError(x) }
        .lift(statusCode)
    }
  }
}
