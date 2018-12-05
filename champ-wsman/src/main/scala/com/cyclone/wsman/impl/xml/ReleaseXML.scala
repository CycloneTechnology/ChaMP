package com.cyclone.wsman.impl.xml

import scala.xml.Elem
import com.cyclone.wsman.impl.Namespace

case class ReleaseXML(enumerationContext: String) extends RequestXMLWithNoDeadline {

  import RequestXML._

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:a={ Namespace.ADDRESSING } xmlns:n={ Namespace.ENUMERATION }>
      <s:Header>
        <a:Action>http://schemas.xmlsoap.org/ws/2004/09/enumeration/Release</a:Action>
        <a:MessageID>{ uuid }</a:MessageID>
        <a:To>{ toAddress }</a:To>
        <a:ReplyTo>
          <a:Address>{ Namespace.REPLY_ANON }</a:Address>
        </a:ReplyTo>
      </s:Header>
      <s:Body>
        <n:Release>
          <n:EnumerationContext>{ enumerationContext }</n:EnumerationContext>
        </n:Release>
      </s:Body>
    </s:Envelope>
}