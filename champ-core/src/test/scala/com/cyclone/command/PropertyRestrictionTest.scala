package com.cyclone.command

import org.junit.Test
import org.scalatest.junit.JUnitSuite

class PropertyRestrictionTest extends JUnitSuite {
  import PropertyRestriction._

  @Test
  def contains: Unit = {
    assert(restrictedTo("A", "B", "C").containsProperty("B") === true)
    assert(restrictedTo("A", "B", "C").containsProperty("D") === false)
  }

  @Test
  def contains_caseIgnored: Unit = {
    assert(restrictedTo("a", "B", "c").containsProperty("A") === true)
    assert(restrictedTo("a", "B", "c").containsProperty("b") === true)
  }

  @Test
  def contains_trimmed: Unit = {
    assert(restrictedTo("A", "B", "C").containsProperty(" B ") === true)
    assert(restrictedTo("A", " B ", "C").containsProperty("B") === true)
  }

  @Test
  def filtersProperties_ordersPropertiesAsInRestriction: Unit = {
    assert(restrictedTo("A", "B", "C").filterProperties("C", "B", "A", "D") === List("A", "B", "C"))
  }

  @Test
  def filtersProperties_removesProperties: Unit = {
    assert(restrictedTo("A", "B", "C").filterProperties("A", "B", "C", "D") === List("A", "B", "C"))
  }

  @Test
  def filtersProperties_onlyIncludesPropertiesInAllProperties: Unit = {
    assert(restrictedTo("A", "B", "C").filterProperties("A", "B") === List("A", "B"))
  }

  @Test
  def filtersProperties_caseIgnored: Unit = {
    assert(restrictedTo("a", "b", "c").filterProperties("A", "B", "C", "D") === List("A", "B", "C"))
  }

  @Test
  def filtersProperties_trimmed: Unit = {
    assert(restrictedTo(" a ", " b ", " c ").filterProperties(" A ", " B ", " C ", " D ") === List(" A ", " B ", " C "))
  }
}
