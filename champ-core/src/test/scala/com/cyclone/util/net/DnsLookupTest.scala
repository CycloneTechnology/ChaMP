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

  val goodDnsConfig = DnsConfig(
    Seq("8.8.8.8"),
    Seq("google.com"),
    1.seconds
  )

  val badDnsConfig= DnsConfig(
    Seq("10.0.0.123"),
    Seq("some.domain"),
    100.millis
  )

  class Fixture(_dnsConfig: DnsConfig = goodDnsConfig)
      extends Dns4sDnsLookupComponent
      with TestActorSystemComponent
      with DnsConfigSourceComponent {

    def dnsConfigSource: DnsConfigSource = new DnsConfigSource {
      def dnsConfig: Future[DnsConfig] = Future.successful(_dnsConfig)
    }
  }

  //  class Fixture extends JavaNamingDnsLookupComponent

  "DnsLookup" when {
    "looking up PTR records" must {
      "work when single host name for address" in new Fixture {
        dnsLookup.lookupPTRs("8.8.8.8").futureValue should contain only
        DnsRecord.PTR("google-public-dns-a.google.com")
      }

      "work when multiple host names for address" ignore new Fixture {
        dnsLookup.lookupPTRs("10.0.0.161").futureValue should contain allOf (DnsRecord.PTR(
          "testdb.cyclone-technology.com"
        ),
        DnsRecord.PTR("dbtest.cyclone-technology.com"))
      }

      "get empty collection when no PTR records for address" in new Fixture {
        dnsLookup.lookupPTRs("10.0.0.222").futureValue shouldBe empty
      }
    }

    "looking up address and then host name PTR records" must {
      "work for addresses" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("8.8.8.8").futureValue should contain only
        DnsRecord.PTR("google-public-dns-a.google.com")
      }

      "work for already fully qualified host names" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("google-public-dns-a.google.com").futureValue should contain only
        DnsRecord.PTR("google-public-dns-a.google.com")
      }

      "work for host names" in new Fixture {
        dnsLookup.lookupAddressAndPTRs("google-public-dns-a").futureValue should contain only
        DnsRecord.PTR("google-public-dns-a.google.com")
      }
    }

    "looking up MX records" must {

      "gets mail server address for email address in mx record" in new Fixture {
        dnsLookup.lookupMXs("gmail.com").futureValue should
        contain inOrder (DnsRecord.MX("alt1.gmail-smtp-in.l.google.com", 10),
        DnsRecord.MX("alt2.gmail-smtp-in.l.google.com", 20),
        DnsRecord.MX("alt3.gmail-smtp-in.l.google.com", 30),
        DnsRecord.MX("alt4.gmail-smtp-in.l.google.com", 40))
      }

      "gets no mail server address for unknown email domain" in new Fixture {
        dnsLookup.lookupMXs("xblahblahx.com").futureValue shouldBe empty
      }
    }
  }

  "inaccessible dns server" must {
    "return empty list of results" in new Fixture(badDnsConfig){
      dnsLookup.lookupPTRs("1.2.3.4").futureValue shouldBe empty
      dnsLookup.lookupAddressAndPTRs("1.2.3.4").futureValue shouldBe empty
      dnsLookup.lookupMXs("gmail.com").futureValue shouldBe empty
    }
  }
}
