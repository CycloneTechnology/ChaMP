package com.cyclone.wsman.command

import com.cyclone.command.PropertyRestriction
import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.InstanceFilter
import com.cyclone.wsman.impl.model.ManagedReference

case class EnumerateByWQL(
  queryString: String,
  maxElementsPerEnumeration: Int = 10,
  resolveReferences: Boolean = false,
  baseResourceUri: ResourceUri = ResourceUri.defaultBase,
  cimNamespace: Option[String] = None
) extends WSManEnumerationQueryDefn

object EnumerateByWQL {

  implicit object Executor extends WSManEnumerationQueryDefn.Executor[EnumerateByWQL] {
    protected def resolveReferences(query: EnumerateByWQL): Boolean =
      query.resolveReferences

    protected def propertyRestriction(query: EnumerateByWQL): PropertyRestriction =
      PropertyRestriction.NoRestriction

    protected def resourceUriReference(query: EnumerateByWQL): ManagedReference =
      ManagedReference(query.baseResourceUri.allClassesUriFrom)

    protected def instanceFilter(query: EnumerateByWQL): InstanceFilter =
      InstanceFilter.ForWQL(query.queryString)
  }

}
