package com.cyclone.wsman.impl.model

import com.cyclone.command.Selector
import com.cyclone.wsman.ResourceUri
import org.junit.Test
import org.scalatest.junit.JUnitSuite

class ManagedReferenceTest extends JUnitSuite {

  @Test
  def checkGetResourceURI(): Unit = {
    val mr = ManagedReference(ResourceUri("https://someResourceURI"))

    assert(mr.getResourceURI === "https://someResourceURI")
  }

  @Test
  def checkGetSelectorValue(): Unit = {
    val mr = ManagedReference(ResourceUri("https://someResourceURI"))
      .addSelector(Selector("name", "value"))

    mr.printXml()

    assert(mr.getSelectorValue("name") === "value")
  }
}
