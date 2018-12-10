package com.cyclone.wsman.impl.xml

import com.cyclone.command.{OperationDeadline, SelectorClause}
import com.cyclone.wsman.impl.model.ManagedReference
import com.cyclone.wsman.impl.{EnumerationMode, InstanceFilter, Namespace}

import scala.xml.Elem

case class EnumXML(
  ref: ManagedReference,
  selectors: SelectorClause,
  filter: InstanceFilter,
  enumMode: EnumerationMode,
  deadline: OperationDeadline
) extends RequestXMLWithDeadline {

  import RequestXML._

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:a={ Namespace.ADDRESSING } xmlns:n={ Namespace.ENUMERATION } xmlns:w={ Namespace.WSMAN }>
      <s:Header>
        <a:To>{ toAddress }</a:To>
        <w:ResourceURI s:mustUnderstand="true">{ ref.getResourceURI }</w:ResourceURI>
        <a:ReplyTo>
          <a:Address s:mustUnderstand="true">{ Namespace.REPLY_ANON }</a:Address>
        </a:ReplyTo>
        <a:Action s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/09/enumeration/Enumerate</a:Action>
        { maxEnvelopeSizeElt }
        <a:MessageID>{ uuid }</a:MessageID>
        <w:Locale xml:lang="en-US" s:mustUnderstand="false"/>
        { timeoutElt }
        { selectorSetElt(selectors) }
      </s:Header>
      <s:Body>
        <n:Enumerate>
          <w:MaxElements>20</w:MaxElements>
          { filter.filterElements }
          <w:EnumerationMode>{ enumMode.name }</w:EnumerationMode>
        </n:Enumerate>
      </s:Body>
    </s:Envelope>

}