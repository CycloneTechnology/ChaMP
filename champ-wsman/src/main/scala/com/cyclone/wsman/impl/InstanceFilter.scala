package com.cyclone.wsman.impl

import com.cyclone.command.{Selector, SelectorClause}

import scala.xml.NodeSeq

/**
  * Controls filtering of selected instances
  *
  * @author Jeremy.Stone
  */
trait InstanceFilter {
  def filterElements: NodeSeq
}

object InstanceFilter {

  def forSelectorClause(selectorClause: SelectorClause): InstanceFilter =
    if (selectorClause.isEmpty) All
    else ForSelectors(selectorClause.selectors)

  object All extends InstanceFilter {
    def filterElements: NodeSeq = NodeSeq.Empty
  }

  case class ForSelectors(selectors: Set[Selector]) extends InstanceFilter {
    require(selectors.nonEmpty, "Selectors set must not be empty")

    def filterElements: NodeSeq =
      // @formatter:off
      <w:Filter Dialect={FilterDialect.SELECTOR}>
        <w:SelectorSet>{for (
          selector <- selectors
            ) yield <w:Selector Name={ selector.name }>{ selector.value }</w:Selector>
          }</w:SelectorSet>
      </w:Filter>
    // @formatter:on
  }

  case class ForWQL(queryString: String) extends InstanceFilter {

    def filterElements: NodeSeq =
      // @formatter:off
      <w:Filter Dialect={ FilterDialect.WQL }>{ queryString }</w:Filter>
    // @formatter:on

  }

}
