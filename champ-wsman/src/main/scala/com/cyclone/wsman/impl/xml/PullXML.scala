package com.cyclone.wsman.impl.xml

import com.cyclone.command.OperationDeadline
import com.cyclone.wsman.impl.{EnumerationParameters, Namespace}
import com.cyclone.wsman.impl.model.ManagedReference

import scala.xml.Elem

case class PullXML(ref: ManagedReference, context: String, parameters: EnumerationParameters)
    extends RequestXMLWithDeadline {

  import RequestXML._

  val deadline: OperationDeadline = parameters.deadline

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:a={ Namespace.ADDRESSING } xmlns:n={ Namespace.ENUMERATION } xmlns:w={ Namespace.WSMAN }>
      <s:Header>
        <a:To>{ toAddress }</a:To>
        <w:ResourceURI s:mustUnderstand="true">{ ref.getResourceURI }</w:ResourceURI>
        <a:ReplyTo>
          <a:Address s:mustUnderstand="true">{ Namespace.REPLY_ANON }</a:Address>
        </a:ReplyTo>
        <a:Action s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/09/enumeration/Pull</a:Action>
        { maxEnvelopeSizeElt }
        <a:MessageID>{ uuid }</a:MessageID>
        <w:Locale xml:lang="en-US" s:mustUnderstand="false"/>
        { timeoutElt }
      </s:Header>
      <s:Body>
        <n:Pull>
          <n:EnumerationContext>{ context }</n:EnumerationContext>
          <n:MaxElements>{ parameters.maxElements }</n:MaxElements>
        </n:Pull>
      </s:Body>
    </s:Envelope>

}