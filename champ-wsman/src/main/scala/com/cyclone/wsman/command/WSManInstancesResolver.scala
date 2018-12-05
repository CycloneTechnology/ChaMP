package com.cyclone.wsman.command

import com.cyclone.command.PropertyRestriction
import com.cyclone.util.OperationDeadline
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.model.{ManagedInstance, ManagedReference}
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Base trait for a query or a subscription that retrieves/receives
  * results containing WSMan managed instances.
  */
trait WSManInstancesResolver[Command] {

  protected def resolveReferences(command: Command): Boolean

  protected def propertyRestriction(command: Command): PropertyRestriction

  protected def resolveReferencesIfReqd(
    command: Command,
    context: WSManOperationContext,
    inst: ManagedInstance,
    cimNamespace: Option[String],
    deadline: OperationDeadline
  ): Future[WSManErrorOr[ManagedInstance]] = {
    def predicate(name: String, ref: ManagedReference) =
      propertyRestriction(command).containsProperty(name)

    if (resolveReferences(command))
      context.resolveReferences(inst, deadline)(predicate)
    else
      inst.right.point[Future]
  }
}
