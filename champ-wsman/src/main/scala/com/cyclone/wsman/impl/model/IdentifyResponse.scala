package com.cyclone.wsman.impl.model

import com.cyclone.util.XmlUtils
import com.cyclone.util.XmlUtils.elementText
import com.cyclone.wsman.command.WSManIdentifyResult

import scala.xml.Elem

/**
  * Raw response to an Identify operation
  */
case class IdentifyResponse(identifyResponse: Elem) {
  def external: WSManIdentifyResult =
    WSManIdentifyResult(
      protocolVersion = protocolVersion,
      productVendor = productVendor,
      productVersion = productVersion,
      securityProfileNames = securityProfileNames
    )

  def protocolVersion: Option[String] =
    elementText(identifyResponse \ "ProtocolVersion")

  def productVendor: Option[String] =
    elementText(identifyResponse \ "ProductVendor")

  def productVersion: Option[String] =
    elementText(identifyResponse \ "ProductVersion")

  def securityProfileNames: Seq[String] =
    (identifyResponse \ "SecurityProfiles" \ "SecurityProfileName").map(_.text)

  override def toString: String = XmlUtils.prettyPrint(identifyResponse)
}
