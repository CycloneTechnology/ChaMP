package com.cyclone.wsman.subscription

import com.cyclone.command.PropertyRestriction
import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.InstanceFilter
import com.cyclone.wsman.impl.model.ManagedReference

import scala.concurrent.duration.{FiniteDuration, _}

case class SubscribeByWQL(
  queryString: String,
  baseResourceUri: ResourceUri = ResourceUri.defaultBase,
  resolveReferences: Boolean = false,
  resolutionTimeout: FiniteDuration = 5.seconds,
  cimNamespace: Option[String] = None
) extends WSManFilteredSubscriptionDefn

object SubscribeByWQL {

  implicit object Executor extends WSManFilteredSubscriptionDefn.Executor[SubscribeByWQL] {

    protected def resourceUriReference(sub: SubscribeByWQL): ManagedReference =
      ManagedReference(sub.baseResourceUri.allClassesUriFrom)

    protected def instanceFilter(sub: SubscribeByWQL): InstanceFilter =
      InstanceFilter.ForWQL(sub.queryString)

    protected def resolveReferences(command: SubscribeByWQL): Boolean =
      command.resolveReferences

    protected def propertyRestriction(command: SubscribeByWQL): PropertyRestriction =
      PropertyRestriction.NoRestriction
  }

}
