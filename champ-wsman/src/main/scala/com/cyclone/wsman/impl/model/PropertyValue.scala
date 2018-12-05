package com.cyclone.wsman.impl.model

sealed trait PropertyValue

case class ReferencePropertyValue(ref: ManagedReference) extends PropertyValue

case class InstancePropertyValue(inst: ManagedInstance) extends PropertyValue

case class StringPropertyValue(value: String) extends PropertyValue

case class ListPropertyValue[V <: PropertyValue](values: List[V]) extends PropertyValue {
  require(requirement = !values.exists(_.isInstanceOf[ListPropertyValue[_]]), message = "Can't wrap list properties with list properties")

  require(values == Nil || values.map(_.getClass).toSet.size == 1, "Heterogeneous list properties are not permitted")

  def containsSimpleValues: Boolean = values != Nil && values.head.getClass == classOf[StringPropertyValue]
}

object ListPropertyValue {
  @annotation.varargs
  def fromStrings(values: String*): ListPropertyValue[StringPropertyValue] = ListPropertyValue(values.map(
    v => StringPropertyValue(v)).toList)

  def apply[V <: PropertyValue](vs: V*): ListPropertyValue[V] = ListPropertyValue(vs.toList)
}