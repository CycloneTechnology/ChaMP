package com.cyclone.wsman.command

import com.cyclone.command.{PropertyRestriction, SelectorClause}
import com.cyclone.wsman.{ResourceUri, WSManOperationContext}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.impl.model.{ManagedInstance, ManagedReference}
import com.cyclone.wsman.impl.WSManOperations
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Get(
  resourceUri: ResourceUri,
  propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
  selectorClause: SelectorClause = SelectorClause.empty,
  resolveReferences: Boolean = false,
  cimNamespace: Option[String] = None
) extends WSManQueryDefn

object Get {

  def fromClassName(
    className: String,
    propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
    selectorClause: SelectorClause = SelectorClause.empty,
    resolveReferences: Boolean = false,
    cimNamespace: Option[String] = None
  ): Get =
    Get(
      ResourceUri.defaultBase.applyRelative(className),
      propertyRestriction,
      selectorClause,
      resolveReferences,
      cimNamespace
    )

  implicit object Executor extends WSManQueryDefn.Executor[Get] {

    protected def resourceUriReference(query: Get): ManagedReference =
      ManagedReference(query.resourceUri)

    protected def resolveReferences(query: Get): Boolean =
      query.resolveReferences

    protected def propertyRestriction(query: Get): PropertyRestriction =
      query.propertyRestriction

    protected def executeInstancesQuery(query: Get, ref: ManagedReference)(
      implicit context: WSManOperationContext
    ): Future[WSManErrorOr[Seq[ManagedInstance]]] = {

      val refWithSelectors =
        ref.applySelectors(
          query.selectorClause ++
          SelectorClause.forCimNamespace(query.cimNamespace)
        )

      val result = for (managedInstance <- eitherT(
                          WSManOperations.get(refWithSelectors, context.operationDeadline)
                        )) yield List(managedInstance)

      result.run
    }
  }

}
