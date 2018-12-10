package com.cyclone.wsman.impl.xml

import com.cyclone.command.OperationDeadline
import com.cyclone.wsman.impl.Namespace

import scala.xml.Elem

case class IdentifyXML(deadline: OperationDeadline) extends RequestXMLWithDeadline {

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:w={ Namespace.WSMAN } xmlns:id={ Namespace.IDENTIFY }>
      <s:Header>
        <w:Locale xml:lang="en-US" s:mustUnderstand="false"/>
        { timeoutElt }
      </s:Header>
      <s:Body>
        <id:Identify/>
      </s:Body>
    </s:Envelope>
}