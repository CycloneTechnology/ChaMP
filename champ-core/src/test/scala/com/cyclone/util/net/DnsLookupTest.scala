package com.cyclone.util.net

import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Future
import scala.concurrent.duration._

class DnsLookupTest
  extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with ActorSystemShutdown {

  class Fixture extends Dns4sDnsLookupComponent
    with TestActorSystemComponent
    with DnsConfigSourceComponent {
    def dnsConfigSource = new DnsConfigSource {
      def dnsConfig: Future[DnsConfig] = Future.successful(
        DnsConfig(
          Seq("10.0.0.4"),
          Seq("cyclone-technology.com"),
          10.seconds
        )
      )
    }
  }

  //  class Fixture extends JavaNamingDnsLookupComponent

  "DnsLookup" when {
    "looking up PTR records" must {
      "work when single host name for address" in new Fixture {
        dnsLookup.lookupPTRs("10.0.0.4").futureValue should contain only
          DnsRecord.PTR("domain-1.cyclone-technology.com")
      }

      "work when multiple host names for address" ignore new Fixture {
        dnsLookup.lookupPTRs("10.0.0.161").futureValue should contain allOf(
          DnsRecord.PTR("testdb.cyclone-technology.com"),
          DnsRecord.PTR("dbtest.cyclone-technology.com"))
      }

      "get empty collection when no PTR records for address" in new Fixture {
        dnsLookup.lookupPTRs("10.0.0.222").futureValue shouldBe empty
      }
    }

    "looking up address and then host name PTR records" must {
      "work for addresses" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("10.0.0.4").futureValue should contain only
          DnsRecord.PTR("domain-1.cyclone-technology.com")
      }

      "work for already fully qualified host names" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("domain-1.cyclone-technology.com").futureValue should contain only
          DnsRecord.PTR("domain-1.cyclone-technology.com")
      }

      "work for host names" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("domain-1").futureValue should contain only
          DnsRecord.PTR("domain-1.cyclone-technology.com")
      }
    }

    "looking up MX records" must {
      "gets mail server address for email address in mx record" in new Fixture {
        dnsLookup.lookupMXs("cyclone-technology.com").futureValue should
          contain(DnsRecord.MX("exchange.cyclone-technology.com", 10))
      }

      "gets no mail server address for unknown email domain" in new Fixture {
        dnsLookup.lookupMXs("xblahblahx.com").futureValue shouldBe empty
      }

      "gets mail server address for email address in mx record - non local address" in new Fixture {
        dnsLookup.lookupMXs("gmail.com").futureValue should
          contain inOrder(
          DnsRecord.MX("alt1.gmail-smtp-in.l.google.com", 10),
          DnsRecord.MX("alt2.gmail-smtp-in.l.google.com", 20),
          DnsRecord.MX("alt3.gmail-smtp-in.l.google.com", 30),
          DnsRecord.MX("alt4.gmail-smtp-in.l.google.com", 40))
      }
    }
  }
}