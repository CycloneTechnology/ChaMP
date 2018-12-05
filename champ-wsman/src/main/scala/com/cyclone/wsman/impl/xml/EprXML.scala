package com.cyclone.wsman.impl.xml

import com.cyclone.wsman.ResourceUri

import scala.xml.Elem
import scala.xml.NodeSeq
import com.cyclone.wsman.impl.Namespace

object EprXML {

  // @formatter:off
  def forResource(resourceUri: ResourceUri): Elem =
    <w:EndpointReference xmlns:w={ Namespace.WSMAN } xmlns:a={ Namespace.ADDRESSING }>
      <a:Address>{ Namespace.REPLY_ANON }</a:Address>
      <a:ReferenceParameters>
        <w:ResourceURI>{ resourceUri.uri }</w:ResourceURI>
        <w:SelectorSet/>
      </a:ReferenceParameters>
    </w:EndpointReference>

  def forAddressAndRef(address: NodeSeq, ref: NodeSeq): Elem =
    <w:EndpointReference xmlns:w={ Namespace.WSMAN } xmlns:a={ Namespace.ADDRESSING }>
      { address }
      { ref }
    </w:EndpointReference>
}