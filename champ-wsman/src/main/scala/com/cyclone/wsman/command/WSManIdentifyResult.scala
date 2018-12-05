package com.cyclone.wsman.command

case class WSManIdentifyResult(
  protocolVersion: Option[String],
  productVendor: Option[String],
  productVersion: Option[String],
  securityProfileNames: Seq[String]
) extends WSManCommandResult
