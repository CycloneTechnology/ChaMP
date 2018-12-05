package com.cyclone.wsman.command

import com.cyclone.command.{PropertyRestriction, SelectorClause}
import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.InstanceFilter
import com.cyclone.wsman.impl.model.ManagedReference

case class EnumerateBySelector(
  resourceUri: ResourceUri,
  propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
  selectorClause: SelectorClause = SelectorClause.empty,
  maxElementsPerEnumeration: Int = 10,
  resolveReferences: Boolean = false,
  cimNamespace: Option[String] = None)
  extends WSManEnumerationQueryDefn

object EnumerateBySelector {

  def fromClassName(className: String,
    propertyRestriction: PropertyRestriction = PropertyRestriction.NoRestriction,
    selectorClause: SelectorClause = SelectorClause.empty,
    maxElementsPerEnumeration: Int = 10,
    resolveReferences: Boolean = false,
    cimNamespace: Option[String] = None): EnumerateBySelector =
    EnumerateBySelector(
      ResourceUri.defaultBase.applyRelative(className),
      propertyRestriction,
      selectorClause,
      maxElementsPerEnumeration,
      resolveReferences,
      cimNamespace)

  implicit object Executor extends WSManEnumerationQueryDefn.Executor[EnumerateBySelector] {
    protected def resolveReferences(query: EnumerateBySelector): Boolean =
      query.resolveReferences

    protected def resourceUriReference(query: EnumerateBySelector): ManagedReference =
      ManagedReference(query.resourceUri)

    protected def propertyRestriction(query: EnumerateBySelector): PropertyRestriction =
      query.propertyRestriction

    protected def instanceFilter(query: EnumerateBySelector): InstanceFilter =
      InstanceFilter.forSelectorClause(query.selectorClause)
  }

}
