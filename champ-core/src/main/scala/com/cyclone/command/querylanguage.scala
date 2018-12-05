package com.cyclone.command

case class Selector(name: String, value: String)

object SelectorClause {
  def empty: SelectorClause = SelectorClause(Set.empty)

  def forCimNamespace(cimNamespace: Option[String]): SelectorClause =
    SelectorClause(cimNamespace.map(ns => Selector("__cimnamespace", ns)).toSet)
}

case class SelectorClause(selectors: Set[Selector]) {
  def isEmpty: Boolean = selectors.isEmpty

  def +(selector: Selector): SelectorClause = SelectorClause(selectors + selector)

  def ++(ss: SelectorClause): SelectorClause = SelectorClause(selectors ++ ss.selectors)
}

/**
  * Property restrictions to allow determination of properties to include in results of
  * queries and event subscriptions.
  *
  * Also used to restrict references that are resolved (if this is specified in the query or subscription).
  *
  * (Apply only to non-WQL queries/subscriptions currently)
  */
sealed trait PropertyRestriction {
  def containsProperty(name: String): Boolean

  def filterProperties(allNames: Iterable[String]): Seq[String]

  def filterProperties(allNames: String*): Seq[String] = filterProperties(allNames.toIterable)

  def toArrayOrNull: Array[String]
}

object PropertyRestriction {
  def restrictedTo(names: String*) = RestrictedTo(names.toList)

  case object NoRestriction extends PropertyRestriction {
    def containsProperty(name: String): Boolean = true

    def filterProperties(allNames: Iterable[String]): Seq[String] = allNames.toSeq

    def toArrayOrNull: Null = null
  }

  case class RestrictedTo(names: Seq[String]) extends PropertyRestriction {
    private lazy val mappedByLCName = mapByTrimmedLCName(names)

    def containsProperty(name: String): Boolean = mappedByLCName.contains(name.trim.toLowerCase)

    def filterProperties(allNames: Iterable[String]): Seq[String] = {
      val allMappedByLCName = mapByTrimmedLCName(allNames)

      for (Some(name) <- names.map(n => allMappedByLCName.get(n.trim.toLowerCase))) yield name
    }

    def toArrayOrNull: Array[String] = names.toArray

    private def mapByTrimmedLCName(ns: Iterable[String]) =
      ns.map(name => (name.trim.toLowerCase, name))
        .groupBy(_._1)
        .mapValues(_.head._2)
  }

}
