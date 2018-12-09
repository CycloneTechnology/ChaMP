package com.cyclone.wsman

import akka.actor.ActorSystem
import com.cyclone.akka._
import com.cyclone.command.PropertyRestriction
import com.cyclone.util.OperationDeadline
import com.cyclone.util.net.{AuthenticationMethod, HttpUrl, JavaNamingDnsLookupComponent, PasswordSecurityContext}
import com.cyclone.wsman.impl.http.settings.ConfigHttpSettingsComponent
import com.cyclone.wsman.impl.http.{DefaultWSManConnectionFactoryComponent, DefaultAsyncHttpClientComponent}
import com.cyclone.wsman.impl.model._
import com.cyclone.wsman.impl.subscription.push.{
  DefaultPushDeliveryRouterComponent,
  GuavaKerberosTokenCacheComponent,
  KerberosStateHousekeeperComponent
}
import org.hamcrest.Matchers._
import org.hamcrest.{FeatureMatcher, Matcher}
import org.jmock.AbstractExpectations._
import org.jmock.lib.concurrent.Synchroniser
import org.jmock.{Expectations, Mockery}
import org.junit.Test
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitSuiteLike
import org.scalatest.{Inside, Matchers}
import scalaz.Scalaz._
import scalaz.{-\/, \/-}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Tests for [[WSManOperationContext]]
  *
  * @author Jeremy.Stone
  */
class WSManOperationContextTest
    extends TestKitSupport
    with JUnitSuiteLike
    with ScalaFutures
    with Matchers
    with Inside
    with ActorSystemShutdown {

  import ManagedInstanceTest._

  class Fixture extends TestActorSystemComponent {
    self =>
    private val mockery = new Mockery {
      setThreadingPolicy(new Synchroniser)
    }

    // Choose a non-specific deadline to check it gets passed round
    val dl = OperationDeadline.fromNow(10 minutes)
    val url = HttpUrl.fromString("http://localhost/wsman")
    val securityContext = PasswordSecurityContext("user", "password", AuthenticationMethod.Basic)
    val referenceResolver = mockery.mock(classOf[ReferenceResolver])

    implicit val context: WSManOperationContext =
      new DefaultWSManContextFactoryComponent with DefaultWSManConnectionFactoryComponent
      with DefaultAsyncHttpClientComponent with ConfigHttpSettingsComponent with DefaultPushDeliveryRouterComponent
      with KerberosStateHousekeeperComponent with GuavaKerberosTokenCacheComponent with ReferenceResolveComponent
      with ActorSystemComponent with ActorMaterializerComponent with JavaNamingDnsLookupComponent {
        val referenceResolver = self.referenceResolver

        implicit def actorSystem: ActorSystem = self.actorSystem
      }.wsmanOperationContextFactory.wsmanContextFor(url, securityContext, dl)

    def willGetReference(ref: ManagedReference, referenced: ManagedInstance) = {
      mockery.checking(new Expectations {
        e =>
        oneOf(referenceResolver)
          .get(`with`(managedReferenceWithUrl(ref)), `with`(equalTo(dl)))(`with`(equalTo(context)))
        will(returnValue(Future.successful(referenced.right)))
      })
    }

    def willFailToGetReference(ref: ManagedReference, err: WSManError) = {
      mockery.checking(new Expectations {
        e =>
        oneOf(referenceResolver).get(`with`(managedReferenceWithUrl(ref)), `with`(equalTo(dl)))(
          `with`(equalTo(context))
        )
        will(returnValue(Future.successful(err.left)))
      })
    }

    def managedReferenceWithUrl(ref: ManagedReference): Matcher[ManagedReference] =
      new FeatureMatcher[ManagedReference, String](equalTo(ref.getResourceURI), "", "") {
        def featureValueOf(r: ManagedReference) = r.getResourceURI
      }
  }

  @Test
  def resolveAllReferences_getsReference(): Unit = new Fixture {
    val ref = newReference("http://refUri")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref", ReferencePropertyValue(ref))

    val referenced = newInstance("refd1").withProperty("r1", "v1")

    willGetReference(ref, referenced)

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref").get, "r1", "v1")
    }
  }

  @Test
  def resolveAllReferences_failsWhenFailsToGetReference(): Unit = new Fixture {
    val ref = newReference("http://refUri")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref", ReferencePropertyValue(ref))

    val referenced = newInstance("refd1").withProperty("r1", "v1")

    willFailToGetReference(ref, WSManIOError(Some("FAILED"), None))

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case -\/(e) => e shouldBe a[WSManIOError]
    }
  }

  @Test
  def resolveAllReferences_getsMultipleReferences(): Unit = new Fixture {
    val ref1 = newReference("http://refUri1")
    val ref2 = newReference("http://refUri2")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref1", ReferencePropertyValue(ref1))
      .withProperty("ref2", ReferencePropertyValue(ref2))

    val referenced1 = newInstance("refd1").withProperty("r1", "v1")
    val referenced2 = newInstance("refd2").withProperty("r2", "v2")

    willGetReference(ref1, referenced1)
    willGetReference(ref2, referenced2)

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref1").get, "r1", "v1")
        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref2").get, "r2", "v2")
    }
  }

  @Test
  def resolveAllReferences_cachesRepeatedReferences(): Unit = new Fixture {
    val ref = newReference("http://refUri")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref1", ReferencePropertyValue(ref))
      .withProperty("ref2", ReferencePropertyValue(ref))

    val referenced = newInstance("refd1").withProperty("r1", "v1")

    willGetReference(ref, referenced)

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref1").get, "r1", "v1")
        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref2").get, "r1", "v1")
    }
  }

  @Test
  def resolveAllReferences_inListValues(): Unit = new Fixture {
    val ref1 = newReference("http://refUri1")
    val ref2 = newReference("http://refUri2")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("list", ListPropertyValue(ReferencePropertyValue(ref1), ReferencePropertyValue(ref2)))

    val referenced1 = newInstance("refd1").withProperty("r1", "v1")
    val referenced2 = newInstance("refd2").withProperty("r2", "v2")

    willGetReference(ref1, referenced1)
    willGetReference(ref2, referenced2)

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        val ListPropertyValue(List(InstancePropertyValue(refd1), InstancePropertyValue(refd2))) =
          instResolved.getPropertyValue("list").get

        assert(refd1.getPropertyValue("r1").get === StringPropertyValue("v1"))
        assert(refd2.getPropertyValue("r2").get === StringPropertyValue("v2"))
    }
  }

  @Test
  def resolveAllReferences_inListValues_cachesRepeatedReferences(): Unit = new Fixture {
    val ref = newReference("http://refUri")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("list", ListPropertyValue(ReferencePropertyValue(ref), ReferencePropertyValue(ref)))

    val referenced = newInstance("refd").withProperty("r1", "v1")

    willGetReference(ref, referenced)

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        val ListPropertyValue(List(InstancePropertyValue(refd1), InstancePropertyValue(refd2))) =
          instResolved.getPropertyValue("list").get

        assert(refd1.getPropertyValue("r1").get === StringPropertyValue("v1"))
        assert(refd2.getPropertyValue("r1").get === StringPropertyValue("v1"))
    }
  }

  @Test
  def resolveAllReferences_nonRefListValuesUntouched(): Unit = new Fixture {

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("list", ListPropertyValue(StringPropertyValue("v1"), StringPropertyValue("v2")))

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved === instRaw)
    }
  }

  @Test
  def resolveAllReferences_noReferences_completesImmediatelyWithSameInstance(): Unit = new Fixture {
    val instRaw = newInstance("a").withProperty("name", StringPropertyValue("value"))

    inside(context.resolveAllReferences(instRaw, dl).futureValue) {
      case \/-(instResolved) =>
        assert(instResolved === instRaw)
    }
  }

  @Test
  def resolveAllReferences_honoursPredicate(): Unit = new Fixture {
    val ref1 = newReference("http://refUri1")
    val ref2 = newReference("http://refUri2")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref1", ReferencePropertyValue(ref1))
      .withProperty("ref2", ReferencePropertyValue(ref2))

    val referenced1 = newInstance("refd1").withProperty("r1", "v1")
    val referenced2 = newInstance("refd2").withProperty("r2", "v2")

    willGetReference(ref1, referenced1)
    willGetReference(ref2, referenced2)

    inside(context.resolveReferences(instRaw, dl)((n, _) => n == "ref2").futureValue) {
      case \/-(instResolved) =>
        assert(instResolved.getPropertyValue("name").get === StringPropertyValue("value"))

        checkReferenceWithUri(instResolved.getPropertyValue("ref1").get, ref1.getResourceURI)
        checkInstanceWithStringPropertyValue(instResolved.getPropertyValue("ref2").get, "r2", "v2")
    }
  }

  @Test
  def resolveAllReferences_listValues_honoursPredicate(): Unit = new Fixture {
    val ref1 = newReference("http://refUri1")
    val ref2 = newReference("http://refUri2")

    val instRaw = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("list", ListPropertyValue(ReferencePropertyValue(ref1), ReferencePropertyValue(ref2)))

    val referenced1 = newInstance("refd1").withProperty("r1", "v1")
    val referenced2 = newInstance("refd2").withProperty("r2", "v2")

    willGetReference(ref1, referenced1)
    willGetReference(ref2, referenced2)

    inside(context.resolveReferences(instRaw, dl)((_, _) => false).futureValue) {
      case \/-(instResolved) => assert(instRaw === instResolved)
    }
  }

  import WSManOperationContext._

  @Test
  def deepInstanceValues_simpleValues(): Unit = {
    val a = newInstance("a")
      .withProperty("name1", StringPropertyValue("value1"))
      .withProperty("name2", StringPropertyValue("value2"))

    deepInstanceValues(a) shouldBe List("name1" -> "value1", "name2" -> "value2")
  }

  @Test
  def deepInstanceValues_simpleValues_inorder(): Unit = {
    val a = newInstance("a")
      .withProperty("name1", StringPropertyValue("value1"))
      .withProperty("name2", StringPropertyValue("value2"))

    deepInstanceValues(a) shouldBe List("name1" -> "value1", "name2" -> "value2")
  }

  @Test
  def deepInstanceValues_simpleValues_restrictedProperties(): Unit = {
    val a = newInstance("a")
      .withProperty("name1", StringPropertyValue("value1"))
      .withProperty("name2", StringPropertyValue("value2"))
      .withProperty("name3", StringPropertyValue("value3"))

    deepInstanceValues(a, PropertyRestriction.restrictedTo("name3", "name1")) shouldBe
    List("name3" -> "value3", "name1" -> "value1")
  }

  @Test
  def deepInstanceValues_simpleValues_restrictedProperties_inorder(): Unit = {
    val a = newInstance("a")
      .withProperty("name1", StringPropertyValue("value1"))
      .withProperty("name2", StringPropertyValue("value2"))
      .withProperty("name3", StringPropertyValue("value3"))

    deepInstanceValues(a, PropertyRestriction.restrictedTo("name3", "name1")) shouldBe
    List("name3" -> "value3", "name1" -> "value1")
  }

  @Test
  def deepInstanceValues_simpleListValues(): Unit = {
    val a = newInstance("a")
      .withProperty("name", ListPropertyValue(StringPropertyValue("value_1"), StringPropertyValue("value_2")))

    deepInstanceValues(a) shouldBe List("name[0]" -> "value_1", "name[1]" -> "value_2")
  }

  @Test
  def deepInstanceValues_nestedInstance(): Unit = {
    val b = newInstance("b").withProperty("b_name", StringPropertyValue("b_value"))

    // Add with same name to create list
    val a = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("b", InstancePropertyValue(b))

    deepInstanceValues(a) shouldBe List("name" -> "value", "b.b_name" -> "b_value")
  }

  @Test
  def deepInstanceValues_restrictions_affectPropertiesThatAreNestedInstances(): Unit = {
    val b = newInstance("b").withProperty("b_name", StringPropertyValue("b_value"))

    // Add with same name to create list
    val a = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("b", InstancePropertyValue(b))

    deepInstanceValues(a, PropertyRestriction.restrictedTo("name")) shouldBe List("name" -> "value")
  }

  @Test
  def deepInstanceValues_restrictions_doNotAffectNestedInstancesOwnProperties(): Unit = {
    val b = newInstance("b").withProperty("b_name", StringPropertyValue("b_value"))

    // Add with same name to create list
    val a = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("b", InstancePropertyValue(b))

    deepInstanceValues(a, PropertyRestriction.restrictedTo("name", "b")) shouldBe
    List("name" -> "value", "b.b_name" -> "b_value")
  }

  @Test
  def deepInstanceValues_nestedInstanceList(): Unit = {
    val b0 = newInstance("b0").withProperty("b0_name", StringPropertyValue("b0_value"))

    val b1 = newInstance("b1").withProperty("b1_name", StringPropertyValue("b1_value"))

    // Add with same name to create list
    val a = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("b", ListPropertyValue(InstancePropertyValue(b0), InstancePropertyValue(b1)))

    deepInstanceValues(a) shouldBe List("name" -> "value", "b[0].b0_name" -> "b0_value", "b[1].b1_name" -> "b1_value")
  }

  @Test
  def deepInstanceValues_nestedReferences_leftAs_REF(): Unit = {
    val ref = newReference("http://refUri")

    val inst = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("ref", ReferencePropertyValue(ref))

    deepInstanceValues(inst) shouldBe List("name" -> "value", "ref" -> "<REF>")
  }

  @Test
  def deepInstanceValues_nestedReferenceList_leftAs_REF(): Unit = {
    val ref1 = newReference("http://refUri1")
    val ref2 = newReference("http://refUri2")

    val inst = newInstance("a")
      .withProperty("name", StringPropertyValue("value"))
      .withProperty("refs", ListPropertyValue(ReferencePropertyValue(ref1), ReferencePropertyValue(ref2)))

    deepInstanceValues(inst) shouldBe List("name" -> "value", "refs[0]" -> "<REF>", "refs[1]" -> "<REF>")
  }
}
