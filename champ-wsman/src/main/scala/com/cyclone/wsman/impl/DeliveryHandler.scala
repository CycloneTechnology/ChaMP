package com.cyclone.wsman.impl

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.cyclone.command.SelectorClause
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.model.ManagedReference
import com.cyclone.wsman.impl.xml.{SubscribeXML, UnsubscribeXML}
import com.cyclone.wsman.subscription.SubscriptionId
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.NodeSeq

trait DeliveryHandler {

  type R <: EventSubscriptionRegistration

  val deliveryModeString: String

  def setupDelivery(context: WSManOperationContext, subscriptionRegistration: R): Source[WSManEnumItem, NotUsed]

  def notifyElements(localSubscriptionId: SubscriptionId): NodeSeq = NodeSeq.Empty

  def deliveryParameterElements(localSubscriptionId: SubscriptionId): NodeSeq = NodeSeq.Empty

  def register(
    localSubscriptionId: SubscriptionId,
    ref: ManagedReference,
    cimNamespace: Option[String],
    filter: InstanceFilter)(implicit context: WSManOperationContext): Future[WSManErrorOr[R]] = {
    val result = for (
      out <- eitherT(WSManOperations.executeSoapRequest(
        SubscribeXML(
          ref,
          SelectorClause.forCimNamespace(cimNamespace),
          filter,
          this,
          localSubscriptionId)))
    ) yield {
      val res = out \ "Body" \ "SubscribeResponse"
      val remoteSubscriptionId = (res \ "SubscriptionManager" \ "ReferenceProperties" \ "Identifier").text
      val ctx = (res \ "EnumerationContext").text

      val subscriptionDescriptor = SubscriptionDescriptor(remoteSubscriptionId, localSubscriptionId)

      createRegistration(ref, subscriptionDescriptor, ctx)
    }

    result.run
  }

  protected def createRegistration(
    ref: ManagedReference,
    subscriptionDescriptor: SubscriptionDescriptor,
    ctx: String)(implicit context: WSManOperationContext): R
}

/**
  * Uniquely identifies a subscription
  *
  * @param remoteSubscriptionId the id that the device knows the subscription as
  * @param localSubscriptionId  the id that we generate ourselves for the subscription
  * @author Jeremy.Stone
  */
case class SubscriptionDescriptor(remoteSubscriptionId: String, localSubscriptionId: SubscriptionId)

/**
  * Represents the registration of subscription remote device
  *
  * @author Jeremy.Stone
  */
class EventSubscriptionRegistration(
  subscriptionRef: ManagedReference,
  subscriptionDescriptor: SubscriptionDescriptor)(implicit val context: WSManOperationContext) {

  val localSubscriptionId: SubscriptionId = subscriptionDescriptor.localSubscriptionId

  def unsubscribe: Future[WSManErrorOr[Done]] =
    WSManOperations.executeSoapRequest(UnsubscribeXML(subscriptionRef, subscriptionDescriptor))
      .map {
        _.rightMap(_ => Done)
      }
}

