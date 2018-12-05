package com.cyclone.wsman.impl.model

import com.cyclone.util.OperationDeadline
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.WSManOperations

import scala.concurrent.Future

// Primarily here to allow mocking of the get method to allow
// testing of WSManContext reference resolution
trait ReferenceResolver {
  /**
    * Gets the instance corresponding to a reference
    */
  def get(ref: ManagedReference, deadline: OperationDeadline)
    (implicit context: WSManOperationContext): Future[WSManErrorOr[ManagedInstance]]
}

trait ReferenceResolveComponent {
  def referenceResolver: ReferenceResolver
}

trait OperationsReferenceResolverComponent extends ReferenceResolveComponent {
  def referenceResolver: ReferenceResolver = WSManOperations
}
