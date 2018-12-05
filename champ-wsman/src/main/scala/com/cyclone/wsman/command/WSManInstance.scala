package com.cyclone.wsman.command

import com.cyclone.command.PropertyRestriction
import com.cyclone.util.CaseInsensitive

case class WSManInstance(
  properties: Map[String, WSManPropertyValue]) {
  def stringProperty(name: String): Option[String] =
    properties.get(name).flatMap {
      case WSManPropertyValue.ForString(v) => Some(v)
      case _                               => None
    }

  def restrictedTo(propertyRestriction: PropertyRestriction): WSManInstance =
    propertyRestriction match {
      case PropertyRestriction.NoRestriction       => this
      case PropertyRestriction.RestrictedTo(names) =>
        val ciNames = names.map(CaseInsensitive(_)).toSet
        WSManInstance(properties.filterKeys(name => ciNames.contains(CaseInsensitive(name))))
    }
}

object WSManInstance {
  def apply(
    properties: (String, WSManPropertyValue)*): WSManInstance =
    new WSManInstance(Map(properties: _*))
}

sealed trait WSManPropertyValue

object WSManPropertyValue {

  // May represent another type - e.g. int, boolean but there is typically nothing in the XML
  // to indicate this...
  case class ForString(value: String) extends WSManPropertyValue

  case class ForInstance(instance: WSManInstance) extends WSManPropertyValue

  case class ForReference(uri: String) extends WSManPropertyValue

  case class ForArray(values: WSManPropertyValue*) extends WSManPropertyValue

}
