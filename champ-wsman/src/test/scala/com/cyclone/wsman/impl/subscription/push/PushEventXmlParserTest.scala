package com.cyclone.wsman.impl.subscription.push

import com.cyclone.wsman.impl.subscription.push.PushedMessage.{Heartbeat, Item}
import com.cyclone.wsman.impl.{Action, WSManEnumInstance}
import com.cyclone.wsman.subscription.SubscriptionId
import org.scalatest.{Matchers, WordSpec}

/**
  * Tests for [[PushEventXmlParser]]
  */
class PushEventXmlParserTest extends WordSpec with Matchers {

  val noEventDoc = <s:Envelope xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd" xmlns:e="http://schemas.xmlsoap.org/ws/2004/08/eventing" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope">
    <s:Header>
      <a:To>http://js.cyclone-technology.com:81/wsman/event_receiver/receive?pushId=fd720033-c8fb-43e6-8d6b-644efd0f411e</a:To>
      <a:ReplyTo>
        <a:Address s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address>
      </a:ReplyTo>
      <a:Action s:mustUnderstand="true">http://schemas.dmtf.org/wbem/wsman/1/wsman/Event</a:Action>
      <a:MessageID>uuid:1302F5D7-C373-4105-843F-C9F908BBADE1</a:MessageID>
    </s:Header>
    <s:Body>
    </s:Body>
  </s:Envelope>

  // @formatter:off
  val heartbeatDoc = <s:Envelope xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd" xmlns:e="http://schemas.xmlsoap.org/ws/2004/08/eventing" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope">
    <s:Header>
      <a:To>http://js.cyclone-technology.com:81/wsman/event_receiver/receive?pushId=fd720033-c8fb-43e6-8d6b-644efd0f411e</a:To>
      <a:ReplyTo>
        <a:Address s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address>
      </a:ReplyTo>
      <a:Action s:mustUnderstand="true">{Action.HEARTBEAT}</a:Action>
      <a:MessageID>uuid:1302F5D7-C373-4105-843F-C9F908BBADE1</a:MessageID>
    </s:Header>
    <s:Body>
    </s:Body>
  </s:Envelope>
  // @formatter:on

  val singleEventDoc = <s:Envelope xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd" xmlns:e="http://schemas.xmlsoap.org/ws/2004/08/eventing" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope">
    <s:Header>
      <a:To>http://js.cyclone-technology.com:81/wsman/event_receiver/receive?pushId=fd720033-c8fb-43e6-8d6b-644efd0f411e</a:To>
      <a:ReplyTo>
        <a:Address s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address>
      </a:ReplyTo>
      <a:Action s:mustUnderstand="true">http://schemas.dmtf.org/wbem/wsman/1/wsman/Event</a:Action>
      <a:MessageID>uuid:1302F5D7-C373-4105-843F-C9F908BBADE1</a:MessageID>
    </s:Header>
    <s:Body>
      <p:__InstanceCreationEvent xsi:type="p:__InstanceCreationEvent_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/__InstanceCreationEvent" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <p:TargetInstance xsi:type="p1:CIM_DataFile_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p1="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/CIM_DataFile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        </p:TargetInstance>
      </p:__InstanceCreationEvent>
    </s:Body>
  </s:Envelope>

  val twoEventDoc = <s:Envelope xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wsman.xsd" xmlns:w="http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd" xmlns:e="http://schemas.xmlsoap.org/ws/2004/08/eventing" xmlns:a="http://schemas.xmlsoap.org/ws/2004/08/addressing" xmlns:s="http://www.w3.org/2003/05/soap-envelope">
    <s:Header>
      <a:To>http://js.cyclone-technology.com:81/wsman/event_receiver/receive?pushId=fd720033-c8fb-43e6-8d6b-644efd0f411e</a:To>
      <a:ReplyTo>
        <a:Address s:mustUnderstand="true">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</a:Address>
      </a:ReplyTo>
      <a:Action s:mustUnderstand="true">http://schemas.dmtf.org/wbem/wsman/1/wsman/Event</a:Action>
      <a:MessageID>uuid:1302F5D7-C373-4105-843F-C9F908BBADE1</a:MessageID>
    </s:Header>
    <s:Body>
      <p:__InstanceCreationEvent xsi:type="p:__InstanceCreationEvent_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/__InstanceCreationEvent" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <p:TargetInstance xsi:type="p1:CIM_DataFile_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p1="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/CIM_DataFile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        </p:TargetInstance>
      </p:__InstanceCreationEvent>
      <p:__InstanceCreationEvent xsi:type="p:__InstanceCreationEvent_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/__InstanceCreationEvent" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <p:TargetInstance xsi:type="p1:CIM_DataFile_Type" xmlns:cim="http://schemas.dmtf.org/wbem/wscim/1/common" xmlns:p1="http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/CIM_DataFile" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        </p:TargetInstance>
      </p:__InstanceCreationEvent>
    </s:Body>
  </s:Envelope>

  val parser = new DefaultPushEventXmlParserComponent {}.pushEventXmlParser

  val subscriptionId = SubscriptionId("id")

  "PushEventXmlParser" must {
    "return empty list if there are no events" in {
      parser.messagesFor(noEventDoc, subscriptionId) shouldBe empty
    }

    "work for single event" in {
      val messages = parser.messagesFor(singleEventDoc, subscriptionId)

      messages should have size 1
      val Item(WSManEnumInstance(inst), _) = messages.head
      inst.getResourceURI shouldBe "http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/__InstanceCreationEvent"

    }

    "work for multiple events" in {
      val messages = parser.messagesFor(twoEventDoc, subscriptionId)

      messages should have size 2

      val Item(WSManEnumInstance(inst), _) = messages.head
      inst.getResourceURI shouldBe "http://schemas.microsoft.com/wbem/wsman/1/wmi/root/cimv2/__InstanceCreationEvent"
    }

    "detect heartbeats" in {
      parser.messagesFor(heartbeatDoc, subscriptionId) shouldBe List(Heartbeat(subscriptionId))
    }
  }
}
