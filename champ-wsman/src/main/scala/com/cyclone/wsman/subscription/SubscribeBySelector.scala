package com.cyclone.wsman.subscription

import com.cyclone.command.{PropertyRestriction, SelectorClause}
import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.InstanceFilter
import com.cyclone.wsman.impl.model.ManagedReference

import scala.concurrent.duration.{FiniteDuration, _}

case class SubscribeBySelector(
  resourceUri: ResourceUri,
  propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
  selectorClause: SelectorClause = SelectorClause.empty,
  resolveReferences: Boolean = false,
  resolutionTimeout: FiniteDuration = 5.seconds,
  cimNamespace: Option[String] = None)
  extends WSManFilteredSubscriptionDefn

object SubscribeBySelector {

  def fromClassName(className: String,
    propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
    selectorClause: SelectorClause = SelectorClause.empty,
    resolveReferences: Boolean = false,
    resolutionTimeout: FiniteDuration = 5.seconds,
    cimNamespace: Option[String] = None): SubscribeBySelector =
    SubscribeBySelector(
      ResourceUri.defaultBase.applyRelative(className),
      propertyRestriction,
      selectorClause,
      resolveReferences,
      resolutionTimeout,
      cimNamespace)

  implicit object Executor extends WSManFilteredSubscriptionDefn.Executor[SubscribeBySelector] {

    protected def resourceUriReference(sub: SubscribeBySelector): ManagedReference =
      ManagedReference(sub.resourceUri)

    protected def instanceFilter(sub: SubscribeBySelector): InstanceFilter =
      InstanceFilter.forSelectorClause(sub.selectorClause)

    protected def resolveReferences(command: SubscribeBySelector): Boolean =
      command.resolveReferences

    protected def propertyRestriction(command: SubscribeBySelector): PropertyRestriction =
      command.propertyRestriction
  }

}