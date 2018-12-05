package com.cyclone.util

import scala.xml.{Elem, Node, NodeSeq, PrettyPrinter}

object XmlUtils {
  private val PRETTY_WIDTH = 200
  private val PRETTY_INDENT = 2
  lazy val prettyPrinter = new PrettyPrinter(PRETTY_WIDTH, PRETTY_INDENT)

  def attributeValue(node: Node, namespace: String, name: String): Option[String] = {
    node \ ("@{" + namespace + "}" + name) match {
      case NodeSeq.Empty => None
      case ns: NodeSeq   => Some(ns.text)
    }
  }

  def attributeValue(node: Node, name: String): Option[String] = {
    node \ ("@" + name) match {
      case NodeSeq.Empty => None
      case ns: NodeSeq   => Some(ns.text)
    }
  }

  def elementText(nodes: NodeSeq): Option[String] = {
    nodes match {
      case NodeSeq.Empty => None
      case ns: NodeSeq   => Some(ns.text)
    }
  }

  def elements(nodeSeq: NodeSeq): Seq[Elem] = nodeSeq.flatMap {
    case e: Elem => Some(e)
    case _       => None
  }

  def singleElement(nodeSeq: NodeSeq): Elem = elements(nodeSeq).head

  def asElement(node: Node): Elem = node.asInstanceOf[Elem]

  def singleChildOfSingleElement(nodeSeq: NodeSeq): Elem =
    singleElement(singleElement(nodeSeq).child)

  def childElements(node: Node): Seq[Elem] = elements(node.child)

  def prettyPrint(nodes: Seq[Node]): String =
    prettyPrinter.formatNodes(nodes)

  def prettyPrint(elem: Elem): String =
    prettyPrinter.formatNodes(elem)
}
