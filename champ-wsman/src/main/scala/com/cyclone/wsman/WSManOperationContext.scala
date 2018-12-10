package com.cyclone.wsman

import akka.stream.Materializer
import com.cyclone.akka.MaterializerComponent
import com.cyclone.command.{OperationDeadline, PropertyRestriction}
import com.cyclone.util.concurrent.Futures
import com.cyclone.util.net.{HttpUrl, SecurityContext}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.impl.http.{WSManConnection, WSManConnectionFactoryComponent}
import com.cyclone.wsman.impl.model._
import com.cyclone.wsman.impl.subscription.push.{PushDeliveryRouter, PushDeliveryRouterComponent}
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.{EitherT, Memo}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WSManOperationContext {

  def deepInstanceValues(
    inst: ManagedInstance,
    propertyRestriction: PropertyRestriction
  ): List[(String, String)] = {
    val list = ListBuffer[(String, String)]()

    add(inst, list, "", propertyRestriction)

    list.toList
  }

  def deepInstanceValues(inst: ManagedInstance): List[(String, String)] =
    deepInstanceValues(inst, PropertyRestriction.NoRestriction)

  private def add(
    inst: ManagedInstance,
    list: ListBuffer[(String, String)],
    prefix: String,
    propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction
  ): Unit = {

    def addStringValue(name: String, value: PropertyValue): Unit =
      value match {
        case InstancePropertyValue(mi) => add(mi, list, name + ".")
        case ReferencePropertyValue(_) => list += (name -> "<REF>")
        case StringPropertyValue(x)    => list += (name -> x.toString)
        case _                         => // Ignore anything else
      }

    for {
      name <- propertyRestriction.filterProperties(inst.propertyNames)
      pv   <- inst.getPropertyValue(name)
    } pv match {
      case ListPropertyValue(values) =>
        var index = 0
        for (value <- values) {
          addStringValue(prefix + name + "[" + index + "]", value)
          index += 1
        }
      case _: PropertyValue => addStringValue(prefix + name, pv)
    }
  }
}

/**
  * Context passed around for WSMan operations that may consist of multiple commands
  *
  * @author Jeremy.Stone
  */
trait WSManOperationContext {

  /**
    * Stream materializer
    */
  def materializer: Materializer

  /**
    * The connection
    */
  def connection: WSManConnection

  /**
    * Hub to connect sources for push deliveries
    */
  def pushDeliveryHub: PushDeliveryRouter

  /**
    * Resolves references to instances inside a managed instance.
    *
    * Considers references only in the top-level instance.
    * That is, references within instances embedded in the main instance are not resolved.
    *
    * Consequently it also does not recurse if a resolved reference also contains a reference.
    *
    * @param inst      the instance
    * @param predicate allows selection of which references are to be brought back
    * @return a future for the instance with the references filled in
    */
  def resolveReferences(inst: ManagedInstance, deadline: OperationDeadline)(
    predicate: (String, ManagedReference) => Boolean
  ): Future[WSManErrorOr[ManagedInstance]]

  /**
    * Resolves all references. Again non-recursively.
    */
  def resolveAllReferences(
    inst: ManagedInstance,
    deadline: OperationDeadline
  ): Future[WSManErrorOr[ManagedInstance]] =
    resolveReferences(inst, deadline)((_, _) => true)

  // TODO ?maybe provide fully recursive method (that uses this one?) for a specd recursion depth?

  /**
    * For operations (including compound operations) that are to complete by a certain time
    * this can be used in calls to operation methods (get, create etc).
    */
  val operationDeadline: OperationDeadline

  def deadlineReached: Boolean = operationDeadline.currentState().deadlineReached
}

/**
  * Factory for [[WSManOperationContext]]s
  */
trait WSManOperationContextFactory {

  def wsmanContextFor(
    httpUrl: HttpUrl,
    securityContext: SecurityContext,
    operationDeadline: OperationDeadline
  ): WSManOperationContext

  def wsmanContextFor(
    target: WSManTarget,
    operationDeadline: OperationDeadline
  ): WSManOperationContext =
    wsmanContextFor(target.httpUrl, target.securityContext, operationDeadline)
}

trait WSManOperationContextFactoryComponent {
  def wsmanOperationContextFactory: WSManOperationContextFactory
}

trait DefaultWSManContextFactoryComponent extends WSManOperationContextFactoryComponent {
  self: PushDeliveryRouterComponent
    with ReferenceResolveComponent
    with MaterializerComponent
    with WSManConnectionFactoryComponent =>

  lazy val wsmanOperationContextFactory: WSManOperationContextFactory =
    new WSManOperationContextFactory {

      def wsmanContextFor(
        httpUrl: HttpUrl,
        securityContext: SecurityContext,
        operationDeadline: OperationDeadline
      ): DefaultWSManOperationContext = {
        val connection = wsManConnectionFactory.createConnection(httpUrl, securityContext)

        DefaultWSManOperationContext(connection, operationDeadline, materializer)
      }
    }

  private case class DefaultWSManOperationContext(
    connection: WSManConnection,
    operationDeadline: OperationDeadline,
    materializer: Materializer
  ) extends WSManOperationContext {
    ctx =>

    def pushDeliveryHub: PushDeliveryRouter = self.pushDeliveryRouter

    def resolveReferences(inst: ManagedInstance, deadline: OperationDeadline)(
      predicate: (String, ManagedReference) => Boolean
    ): Future[WSManErrorOr[ManagedInstance]] = {

      val lookupRef: ManagedReference => Future[WSManErrorOr[ManagedInstance]] =
        Memo.immutableHashMapMemo { mr =>
          referenceResolver.get(mr, deadline)(ctx)
        }

      def replaceWithReferred(
        mi: ManagedInstance,
        name: String,
        rpv: ReferencePropertyValue
      ): Future[WSManErrorOr[ManagedInstance]] = {
        val ReferencePropertyValue(ref) = rpv

        val result =
          for {
            referred <- eitherT(lookupRef(ref))
          } yield mi.withProperty(name, InstancePropertyValue(referred))

        result.run
      }

      def replaceListWithReferred[V <: PropertyValue](
        mi: ManagedInstance,
        name: String,
        lpv: ListPropertyValue[V]
      ): Future[WSManErrorOr[ManagedInstance]] = {
        val ListPropertyValue(pvs) = lpv

        // List property values are homogeneous so the 'collect' call will not filter
        // out just *some* list items: will do all or none...
        pvs.collect {
          case rpv @ ReferencePropertyValue(ref) if predicate(name, ref) => rpv
        } match {
          case Nil => Future.successful(mi.right)

          case l: List[PropertyValue] =>
            val result = for (outPvs <- eitherT(refsToInstances(l))) yield {
              mi.withProperty(name, ListPropertyValue(outPvs: _*))
            }

            result.run
        }
      }

      def refsToInstances(
        pvsIn: List[ReferencePropertyValue]
      ): Future[WSManErrorOr[Seq[InstancePropertyValue]]] =
        Futures.traverseSerially[ReferencePropertyValue, WSManError, InstancePropertyValue](pvsIn) { pv =>
          val ReferencePropertyValue(ref) = pv

          val result: EitherT[Future, WSManError, InstancePropertyValue] = for {
            mi <- eitherT(lookupRef(ref))
          } yield InstancePropertyValue(mi)

          result.run
        }

      inst
        .propertyNamesAndValues { (n, pv) =>
          pv match {
            case ReferencePropertyValue(ref) => predicate(n, ref)
            case _                           => true
          }
        }
        .foldLeft(Future.successful(inst.right[WSManError])) {
          case (future, (name, rpv: ReferencePropertyValue)) =>
            val result = for {
              mi       <- eitherT(future)
              replaced <- eitherT(replaceWithReferred(mi, name, rpv))
            } yield replaced

            result.run

          case (future, (name, lpv: ListPropertyValue[_])) =>
            val result = for {
              mi       <- eitherT(future)
              replaced <- eitherT(replaceListWithReferred(mi, name, lpv))
            } yield replaced

            result.run

          case (future, _) => future
        }
    }
  }

}
