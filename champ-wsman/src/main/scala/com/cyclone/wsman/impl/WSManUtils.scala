package com.cyclone.wsman.impl

object Namespace {
  val SOAP = "http://www.w3.org/2003/05/soap-envelope"

  val IDENTIFY = "http://schemas.dmtf.org/wbem/wsman/identity/1/wsmanidentity.xsd"

  val MS_FAULT = "http://schemas.microsoft.com/wbem/wsman/1/wsmanfault"

  val WSMAN = "http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd"

  val ADDRESSING = "http://schemas.xmlsoap.org/ws/2004/08/addressing"

  val ENUMERATION = "http://schemas.xmlsoap.org/ws/2004/09/enumeration"

  val EVENTING = "http://schemas.xmlsoap.org/ws/2004/08/eventing"

  val XMLSCHEMA = "http://www.w3.org/2001/XMLSchema"

  val XMLSCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance"

  val CIMCOMMON = "http://schemas.dmtf.org/wbem/wscim/1/common"

  val MS_SHELL = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell"

  val MS_COMMAND = "http://schemas.microsoft.com/wbem/wsman/1/windows/shell/cmd"

  val REPLY_ANON = "http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous"
}

object Action {
  val HEARTBEAT = "http://schemas.dmtf.org/wbem/wsman/1/wsman/Heartbeat"
}

object FilterDialect {
  val SELECTOR = "http://schemas.dmtf.org/wbem/wsman/1/wsman/SelectorFilter"
  val WQL = "http://schemas.microsoft.com/wbem/wsman/1/WQL"

  // Not supported yet...
  //  val CQL = "http://schemas.dmtf.org/wbem/cql/1/dsp0202.pdf"
}

// See http://www.dmtf.org/sites/default/files/standards/documents/DSP0226_1.0.0.pdf
object EventMode {

  // No ack - so will not guarantee delivery order
  val EVENT_MODE_PUSH_SINGLE = "http://schemas.xmlsoap.org/ws/2004/08/eventing/DeliveryModes/Push"

  //  val EVENT_MODE_PUSH_SINGLE_WITH_ACK = "http://schemas.dmtf.org/wbem/wsman/1/wsman/PushWithAck"
  //
  //  val EVENT_MODE_PUSH_BATCHED_WITH_ACK = "http://schemas.dmtf.org/wbem/wsman/1/wsman/Events"

  val EVENT_MODE_PULL = "http://schemas.dmtf.org/wbem/wsman/1/wsman/Pull"
}
