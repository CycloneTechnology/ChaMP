package com.cyclone.wsman.impl.xml

import com.cyclone.command.SelectorClause
import com.cyclone.wsman.impl.model.ManagedReference
import com.cyclone.wsman.impl.{DeliveryHandler, InstanceFilter, Namespace}
import com.cyclone.wsman.subscription.SubscriptionId

import scala.xml.Elem

case class SubscribeXML(
  ref: ManagedReference,
  selectors: SelectorClause,
  filter: InstanceFilter,
  deliveryHandler: DeliveryHandler,
  localSubscriptionId: SubscriptionId
) extends RequestXMLWithNoDeadline {

  import RequestXML._

  // @formatter:off
  override def xml(toAddress: String): Elem =
    <s:Envelope xmlns="" xmlns:s={Namespace.SOAP} xmlns:a={Namespace.ADDRESSING} xmlns:w={Namespace.WSMAN} xmlns:wse={Namespace.EVENTING}>
      <s:Header>
        <a:To>{toAddress}</a:To>
        <w:ResourceURI s:mustUnderstand="true">{ref.getResourceURI}</w:ResourceURI>
        <a:ReplyTo>
          <a:Address s:mustUnderstand="true">{Namespace.REPLY_ANON}</a:Address>
        </a:ReplyTo>
        <a:Action s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe</a:Action>
        <a:MessageID>{uuid}</a:MessageID>
        {selectorSetElt(selectors)}
      </s:Header>
      <s:Body>
        <wse:Subscribe>
          <wse:Delivery Mode={deliveryHandler.deliveryModeString}>
            {deliveryHandler.notifyElements(localSubscriptionId)}
            {deliveryHandler.deliveryParameterElements(localSubscriptionId)}
          </wse:Delivery>
          <wse:Expires>PT1M</wse:Expires>{filter.filterElements}
        </wse:Subscribe>
      </s:Body>
    </s:Envelope>
}