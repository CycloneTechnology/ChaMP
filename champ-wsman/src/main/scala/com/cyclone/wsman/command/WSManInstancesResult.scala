package com.cyclone.wsman.command

import com.cyclone.command.PropertyRestriction

case class WSManInstancesResult(
  instances: Seq[WSManInstance]) extends WSManCommandResult {
  def allPropertyNames: Set[String] =
    instances.flatMap(_.properties.keys).toSet
}

object WSManInstancesResult {
  def apply(instances: Seq[WSManInstance],
    propertyRestriction: PropertyRestriction): WSManInstancesResult =
    WSManInstancesResult(instances.map(_.restrictedTo(propertyRestriction)))
}

