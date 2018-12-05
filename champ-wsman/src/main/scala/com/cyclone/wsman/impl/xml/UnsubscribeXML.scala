package com.cyclone.wsman.impl.xml

import scala.xml.Elem
import com.cyclone.wsman.impl.Namespace
import com.cyclone.wsman.impl.SubscriptionDescriptor
import com.cyclone.wsman.impl.model.ManagedReference

case class UnsubscribeXML(ref: ManagedReference, subscriptionDescriptor: SubscriptionDescriptor)
    extends RequestXMLWithNoDeadline {

  import RequestXML._

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns:s={ Namespace.SOAP } xmlns:a={ Namespace.ADDRESSING } xmlns:w={ Namespace.WSMAN } xmlns:wse={ Namespace.EVENTING }>
      <s:Header>
        <a:To>{ toAddress }</a:To>
        <w:ResourceURI s:mustUnderstand="true">{ ref.getResourceURI }</w:ResourceURI>
        <a:ReplyTo>
          <a:Address s:mustUnderstand="true">{ Namespace.REPLY_ANON }</a:Address>
        </a:ReplyTo>
        <a:Action s:mustUnderstand="true">
          http://schemas.xmlsoap.org/ws/2004/08/eventing/Unsubscribe
        </a:Action>
        <a:MessageID>{ uuid }</a:MessageID>
        <wse:Identifier>{ subscriptionDescriptor.remoteSubscriptionId }</wse:Identifier>
      </s:Header>
      <s:Body>
        <wse:Unsubscribe/>
      </s:Body>
    </s:Envelope>
}