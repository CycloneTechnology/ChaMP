package com.cyclone.wsman.impl.subscription.push

import com.cyclone.util.XmlUtils
import com.cyclone.util.XmlUtils.prettyPrint
import com.cyclone.wsman.impl.subscription.push.PushedMessage.{Heartbeat, Item}
import com.cyclone.wsman.impl.{Action, WSManEnumItem}
import com.cyclone.wsman.subscription.SubscriptionId
import com.typesafe.scalalogging.LazyLogging

import scala.xml.{Elem, Node}

trait PushEventXmlParser {

  /**
    * Parses received XML document containing events or heartbeats
    * to create [[PushedMessage]] objects.
    */
  def messagesFor(document: Elem, localSubscriptionId: SubscriptionId): List[PushedMessage]
}

trait PushEventXmlParserComponent {
  def pushEventXmlParser: PushEventXmlParser
}

trait DefaultPushEventXmlParserComponent extends PushEventXmlParserComponent with LazyLogging {

  lazy val pushEventXmlParser: PushEventXmlParser = new PushEventXmlParser {

    def messagesFor(document: Elem, localSubscriptionId: SubscriptionId): List[PushedMessage] = {
      def items(document: Elem): List[PushedMessage] = {
        logger.debug(s"EventReceiver received:\n${prettyPrint(document)}")

        allEventElems(document)
          .map(WSManEnumItem.fromElement)
          .map(Item(_, localSubscriptionId))
          .toList
      }

      def allEventElems(document: Elem) = {
        for (eventParent <- eventParents(document);
             eventElem   <- XmlUtils.childElements(eventParent)) yield eventElem
      }

      def eventParents(document: Elem): Iterable[Node] = {
        // Batched returns envelope\\body\\events\\event\\...
        // others return envelope\\body\\...
        val body = document \\ "Envelope" \\ "Body"

        (body \\ "Events" \\ "Event").headOption match {
          case Some(events) => events
          case None         => body.head
        }
      }

      (document \\ "Header" \\ "Action").text match {
        case Action.HEARTBEAT => List(Heartbeat(localSubscriptionId))
        case _                => items(document)
      }
    }
  }
}
