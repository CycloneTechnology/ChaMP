package com.cyclone.wsman.impl.model

import com.cyclone.command.{Selector, SelectorClause}
import com.cyclone.util.XmlUtils.{attributeValue, singleElement}
import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.xml.EprXML

import scala.xml.NodeSeq.seqToNodeSeq
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node}

private[wsman] object ManagedReference {
  def apply(resourceUri: ResourceUri): ManagedReference =
    ManagedReference(EprXML.forResource(resourceUri))

  def apply(root: Elem): ManagedReference = {
    new ManagedReference(root)
  }
}

private[wsman] class ManagedReference private(protected[impl] val root: Elem) {

  def getResourceURI: String =
    (root \ "ReferenceParameters" \ "ResourceURI").text

  override def equals(that: Any): Boolean = {
    that match {
      case reference: ManagedReference => reference.getResourceURI == this.getResourceURI
      case _                           => false
    }
  }

  override def hashCode(): Int = getResourceURI.hashCode

  private def addSelector(name: String, value: String): ManagedReference = {

    val res = singleElement(root \ "ReferenceParameters" \ "ResourceURI")

    def addSelectors(existing: Seq[Node]): Seq[Node] =
    // @formatter:off
      existing ++ <Selector Name={name}>{value}</Selector>.copy(prefix = res.prefix)
    // @formatter:on

    object selectionAdder extends RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case Elem(prefix, "SelectorSet", attribs, scope, existingSelectors@_*) =>
          Elem(prefix, "SelectorSet", attribs, scope, false, addSelectors(existingSelectors): _*)
        case other: Node                                                       => other
      }
    }

    ManagedReference(singleElement(new RuleTransformer(selectionAdder).transform(root)))
  }

  def addSelector(selector: Selector): ManagedReference = addSelector(selector.name, selector.value)

  def applySelectors(selectorClause: SelectorClause): ManagedReference =
    applySelectors(selectorClause.selectors)

  def applySelectors(selectors: Iterable[Selector]): ManagedReference =
    selectors.foldLeft(this) {
      case (ref, selector) => ref.addSelector(selector)
    }

  private def selectorSetElements =
    (root \ "ReferenceParameters" \ "SelectorSet" \ "Selector")
      .filter {
        _.attribute("Name").isDefined
      }

  def selectorClause: SelectorClause = {

    val sels = (for (selector <- selectorSetElements) yield {
      Selector((selector \ "@Name").text, selector.text)
    }).toSet

    SelectorClause(sels)
  }

  protected[impl] def printXml(): Unit = println(root)

  def getSelectorValue(name: String): String = {
    val selectorWithName =
      selectorSetElements
        .filter(attributeValue(_, "Name").contains(name))
        .head

    selectorWithName.text
  }
}
