package com.cyclone.wsman.subscription

import akka.stream.scaladsl.{Sink, Source}
import com.cyclone.util.{AbsoluteDeadline, OperationDeadline}
import com.cyclone.wsman.{WSManErrorException, WSManOperationContext}
import com.cyclone.wsman.command._
import com.cyclone.wsman.impl.model.ManagedReference
import com.cyclone.wsman.impl.subscription.SubscriptionItem
import com.cyclone.wsman.impl.{DeliveryHandler, InstanceFilter}
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
  * Defines (in some unspecified manner) what events a subscription is interested in.
  *
  * Call subscribe to subscribe to events and get a WSManSubscription object.
  *
  * @author Jeremy.Stone
  */
trait WSManSubscriptionDefn {
  def resolutionTimeout: FiniteDuration

  //NB this is a function not a value so that we get a fresh deadline 
  // (that is absolute so that it spans getting all references)
  // for each event we receive (since receiving events is open-ended).
  def resolutionDeadline: OperationDeadline = OperationDeadline.fromNow(resolutionTimeout)
}

protected trait WSManFilteredSubscriptionDefn
  extends WSManSubscriptionDefn {

  def cimNamespace: Option[String]
}

object WSManFilteredSubscriptionDefn {

  trait Executor[S <: WSManFilteredSubscriptionDefn]
    extends SubscriptionExecutor[S]
      with WSManInstancesResolver[S] {

    protected def instanceFilter(sub: S): InstanceFilter

    protected def resourceUriReference(sub: S): ManagedReference

    def source(sub: S, deliveryHandler: DeliveryHandler)(implicit context: WSManOperationContext): Source[SubscriptionItem, SubscriptionId] = {

      lazy val localSubscriptionId = SubscriptionId.newId

      Source.fromFuture(
        deliveryHandler.register(
          localSubscriptionId, resourceUriReference(sub), sub.cimNamespace, instanceFilter(sub)))
        .flatMapConcat {
          case \/-(subsRegistration) =>
            val instances = deliveryHandler.setupDelivery(context, subsRegistration)
              .map(_.managedInstance)
              .collect { case Some(x) => x }
              .mapAsync(1) { instance =>
                resolveReferencesIfReqd(sub, context, instance, sub.cimNamespace, sub.resolutionDeadline)
                  .map {
                    case \/-(item) => item
                    case -\/(e)    => throw WSManErrorException(e)
                  }
              }
              .alsoTo(Sink.onComplete(_ => subsRegistration.unsubscribe))
              .map(SubscriptionItem.Instance)

            Source.single(SubscriptionItem.Subscribed) ++ instances

          case -\/(e) => throw WSManErrorException(e)
        }
        .mapMaterializedValue(_ => localSubscriptionId)
    }
  }

}







