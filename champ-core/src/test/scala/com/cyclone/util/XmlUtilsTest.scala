package com.cyclone.util

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import XmlUtils._

class XmlUtilsTest extends JUnitSuite {

  @Test
  def attributeValue_getsUnprefixedAttributes(): Unit = {
    val xml = <d><e name="value"/></d>

    assert(attributeValue((xml \ "e").head, "name") === Some("value"))
    assert(attributeValue((xml \ "e").head, "other") === None)
  }

  @Test
  def attributeValue_doesNotGetPrefixedAttributes() : Unit = {
    val xml = <d xmlns:p="http://someurl"><e p:name="value"/></d>

    assert(attributeValue((xml \ "e").head, "name") === None)
    assert(attributeValue((xml \ "e").head, "other") === None)
  }

  @Test
  def attributeValue_withPrefixMatch_prefixed(): Unit = {
    val ns = "http://someurl"
    val xml = <d xmlns:p="http://someurl"><e p:name="value"/></d>

    assert(attributeValue((xml \ "e").head, ns, "name") === Some("value"))
    assert(attributeValue((xml \ "e").head, ns, "other") === None)
  }

  @Test
  def elements_ignoresTextSiblings(): Unit = {
    val xml = <a>
                someText
                <b>one</b>
                <b>two</b>
                extraText
                <b>three</b>
                moreText
              </a>

    assert(elements(xml \ "b") === <b>one</b><b>two</b><b>three</b>)
  }

  @Test
  def childElements_ignoresTextSiblings() : Unit= {
    val xml = <a>
                someText
                <b>one</b>
                <b>two</b>
                extraText
                <b>three</b>
                moreText
              </a>

    assert(childElements(xml) === <b>one</b><b>two</b><b>three</b>)
  }

  @Test
  def singleElement_getsFirstElement(): Unit = {
    val xml = <a>
                someText
                <b>one</b>
                <b>two</b>
              </a>

    assert(singleElement(xml \ "b") === <b>one</b>)
  }
  @Test
  def singleChildOfSingleElement_returnsFirstChildOfFirstChild(): Unit = {
    val xml = <a>
                <Body>
                  <b>text</b>
                </Body>
              </a>

    assert(singleChildOfSingleElement(xml \ "Body") === <b>text</b>)
  }
}