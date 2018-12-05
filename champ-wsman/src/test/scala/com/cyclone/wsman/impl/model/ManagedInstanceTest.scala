package com.cyclone.wsman.impl.model

import com.cyclone.wsman.ResourceUri
import com.cyclone.wsman.impl.Namespace
import com.cyclone.wsman.command.{WSManInstance, WSManPropertyValue}
import org.scalatest.{Matchers, WordSpec}

object ManagedInstanceTest extends Matchers {

  def newInstance(namespace: String, name: String, prefix: String = "p") =
    ManagedInstance(name, namespace, prefix)

  def newInstance(name: String): ManagedInstance =
    ManagedInstance(name, "http://someNamespace")

  def newReference(refUri: String) =
    ManagedReference(ResourceUri(refUri))

  def checkInstanceWithStringPropertyValue(pv: PropertyValue, name: String, value: String) = {
    val InstancePropertyValue(mi) = pv
    assert(mi.getPropertyValue(name) === Some(StringPropertyValue(value)))
  }

  def checkReferenceWithUri(pv: PropertyValue, uri: String) = {
    val ReferencePropertyValue(ref) = pv
    assert(ref.getResourceURI === uri)
  }
}

class ManagedInstanceTest extends WordSpec with Matchers {

  import ManagedInstanceTest._

  "ManagedInstance" when {

    "getting the external representation" must {
      "work for string properties" in {
        val a = newInstance("a")
          .withProperty("name", StringPropertyValue("value"))

        a.external shouldBe WSManInstance(
          "name" -> WSManPropertyValue.ForString("value")
        )
      }

      "work for instance properties" in {
        val b = newInstance("b")
          .withProperty("b_name", StringPropertyValue("b_value"))

        val a = newInstance("a")
          .withProperty("b", InstancePropertyValue(b))

        a.external shouldBe WSManInstance(
          "b" -> WSManPropertyValue.ForInstance(WSManInstance(
            "b_name" -> WSManPropertyValue.ForString("b_value")
          ))
        )
      }

      "work for dates in nested elements" in {
        val b = newInstance("b", Namespace.CIMCOMMON, "cim")
          .withProperty("Datetime", StringPropertyValue("2014-03-12T15:57:38.164394Z"))

        val a = newInstance("a")
          .withProperty("startDate", InstancePropertyValue(b))

        a.external shouldBe WSManInstance(
          "startDate" -> WSManPropertyValue.ForString("2014-03-12T15:57:38.164394Z")
        )
      }

      "work for references" in {
        val r2 = newReference("http://refUri")

        val a = newInstance("a").withProperty("name", StringPropertyValue("value"))
          .withProperty("ref", ReferencePropertyValue(r2))

        val ReferencePropertyValue(r2_got) = a.getPropertyValue("ref").get

        assert(r2_got.getResourceURI === "http://refUri")

        a.external shouldBe WSManInstance(
          "name" -> WSManPropertyValue.ForString("value"),
          "ref" -> WSManPropertyValue.ForReference("http://refUri")
        )
      }

      "work for lists of simple values" in {
        val a = newInstance("a")
          .withProperty("name",
            ListPropertyValue(StringPropertyValue("value1"), StringPropertyValue("value2"))
          )

        a.external shouldBe WSManInstance(
          "name" -> WSManPropertyValue.ForArray(
            WSManPropertyValue.ForString("value1"),
            WSManPropertyValue.ForString("value2")
          )
        )
      }

      "work for lists of instances" in {
        val b1 = newInstance("b")
          .withProperty("b1_name", StringPropertyValue("b1_value"))
        val b2 = newInstance("b")
          .withProperty("b2_name", StringPropertyValue("b2_value"))

        val a = newInstance("a").withProperty("name",
          ListPropertyValue(InstancePropertyValue(b1), InstancePropertyValue(b2)))

        a.external shouldBe WSManInstance(
          "name" -> WSManPropertyValue.ForArray(
            WSManPropertyValue.ForInstance(WSManInstance("b1_name" -> WSManPropertyValue.ForString("b1_value"))),
            WSManPropertyValue.ForInstance(WSManInstance("b2_name" -> WSManPropertyValue.ForString("b2_value")))
          )
        )
      }

      "work for lists of references" in {
        val r1 = newReference("http://uri1")

        val r2 = newReference("http://uri2")

        val a = newInstance("a").withProperty("name",
          ListPropertyValue(ReferencePropertyValue(r1), ReferencePropertyValue(r2)))

        a.external shouldBe WSManInstance(
          "name" -> WSManPropertyValue.ForArray(
            WSManPropertyValue.ForReference("http://uri1"),
            WSManPropertyValue.ForReference("http://uri2")
          )
        )
      }
    }

    "getting properties" must {
      "work for string properties" in {
        val a = newInstance("a")
          .withProperty("name", StringPropertyValue("value"))

        assert(a.getPropertyValue("name") === Some(StringPropertyValue("value")))
      }

      "work for instance properties" in {
        val b = newInstance("b")
          .withProperty("b_name", StringPropertyValue("b_value"))

        val a = newInstance("a")
          .withProperty("b", InstancePropertyValue(b))

        val InstancePropertyValue(b_got) = a.getPropertyValue("b").get

        assert(b_got.getPropertyValue("b_name") === Some(StringPropertyValue("b_value")))
      }

      "work for dates in nested elements" in {
        val b = newInstance("b", Namespace.CIMCOMMON, "cim")
          .withProperty("Datetime", StringPropertyValue("2014-03-12T15:57:38.164394Z"))

        val a = newInstance("a")
          .withProperty("startDate", InstancePropertyValue(b))

        assert(a.getPropertyValue("startDate") === Some(StringPropertyValue("2014-03-12T15:57:38.164394Z")))
      }

      "work for references" in {
        val r2 = newReference("http://refUri")

        val a = newInstance("a").withProperty("name", StringPropertyValue("value"))
          .withProperty("ref", ReferencePropertyValue(r2))

        val ReferencePropertyValue(r2_got) = a.getPropertyValue("ref").get

        assert(r2_got.getResourceURI === "http://refUri")
      }

      "work for lists of instances" in {
        val b1 = newInstance("b")
          .withProperty("b1_name", StringPropertyValue("b1_value"))
        val b2 = newInstance("b")
          .withProperty("b2_name", StringPropertyValue("b2_value"))

        val a = newInstance("a").withProperty("name", ListPropertyValue(InstancePropertyValue(b1),
          InstancePropertyValue(b2)))

        val Some(ListPropertyValue(b1_got :: b2_got :: _)) = a.getPropertyValue("name")

        checkInstanceWithStringPropertyValue(b1_got, "b1_name", "b1_value")
        checkInstanceWithStringPropertyValue(b2_got, "b2_name", "b2_value")
      }

      "hold list properties" in {
        val p = ListPropertyValue(StringPropertyValue("value_1"),
          StringPropertyValue("value_2"))

        val a = newInstance("a").withProperty("name", p)

        assert(a.getPropertyValue("name") === Some(p))
      }

      "work for lists of references" in {
        val r1 = newReference("http://uri1")

        val r2 = newReference("http://uri2")

        val a = newInstance("a").withProperty("name", ListPropertyValue(ReferencePropertyValue(r1),
          ReferencePropertyValue(r2)))

        val Some(ListPropertyValue(r1_got :: r2_got :: _)) = a.getPropertyValue("name")

        checkReferenceWithUri(r1_got, "http://uri1")
        checkReferenceWithUri(r2_got, "http://uri2")
      }
    }

    "disallow heterogeneous lists" in {
      // Won't be able to discern instance without it having a property..      .      intercept[IllegalArgumentException] {
      intercept[IllegalArgumentException] {
        ListPropertyValue(InstancePropertyValue(newInstance("b")),
          ReferencePropertyValue(newReference("http://refUri")))
      }

      intercept[IllegalArgumentException] {
        ListPropertyValue(StringPropertyValue("value"),
          ReferencePropertyValue(newReference("http://refUri")))
      }

      intercept[IllegalArgumentException] {
        ListPropertyValue(InstancePropertyValue(newInstance("b")),
          StringPropertyValue("value"))
      }
    }

    "getting all properties" must {
      "filter properties by predicate" in {
        val a = newInstance("a")
          .withProperty("name1", StringPropertyValue("value1"))
          .withProperty("name2", StringPropertyValue("value2"))

        assert(a.propertyNamesAndValues((n, _) => n == "name1") ===
          List(("name1", StringPropertyValue("value1"))))
      }

      "get all properties" in {
        val p = ListPropertyValue(StringPropertyValue("value_1"),
          StringPropertyValue("value_2"))

        val a = newInstance("a").withProperty("name", p)

        assert(a.allPropertyNamesAndValues === List(("name", p)))
      }

      "work for lists of simple values" in {
        val a = newInstance("a")
          .withProperty("name1", StringPropertyValue("value1"))
          .withProperty("name2", StringPropertyValue("value2"))

        assert(a.allPropertyNamesAndValues ===
          List(("name1", StringPropertyValue("value1")), ("name2", StringPropertyValue("value2"))))
      }
    }

    "adding a property" must {
      "override existing value" in {
        val a = newInstance("a")
          .withProperty("name", StringPropertyValue("value_1"))
          .withProperty("name", StringPropertyValue("value_2"))

        assert(a.getPropertyValue("name") === Some(StringPropertyValue("value_2")))
      }

      "override existing list property" in {
        val a = newInstance("a")
          .withProperty("name", ListPropertyValue(StringPropertyValue("value_1"), StringPropertyValue("value_2")))
          .withProperty("name", StringPropertyValue("value_3"))

        assert(a.getPropertyValue("name") === Some(StringPropertyValue("value_3")))
      }
    }

    "getting property names" must {
      "work" in {
        val a = newInstance("a")
          .withProperty("name_1", StringPropertyValue("value_1"))
          .withProperty("name_2", StringPropertyValue("value_2"))

        assert(a.propertyNames === List("name_1", "name_2"))
      }

      "return list properties only once" in {
        val a = newInstance("a")
          .withProperty("name", ListPropertyValue(StringPropertyValue("value_1"), StringPropertyValue("value_2")))

        assert(a.propertyNames === List("name"))
      }

      "ignore nested instance properties" in {
        val b = newInstance("b")
          .withProperty("b_name", StringPropertyValue("b_value"))

        val a = newInstance("a")
          .withProperty("name", StringPropertyValue("value_1"))
          .withProperty("b", InstancePropertyValue(b))

        assert(a.propertyNames === List("name", "b"))
      }

      "include dates in nested elements" in {
        val b = newInstance("b", Namespace.CIMCOMMON, "cim")
          .withProperty("Datetime", StringPropertyValue("2014-03-12T15:57:38.164394Z"))

        val a = newInstance("a")
          .withProperty("name_1", StringPropertyValue("value_1"))
          .withProperty("startDate", InstancePropertyValue(b))

        assert(a.propertyNames === List("name_1", "startDate"))
      }
    }
  }

}