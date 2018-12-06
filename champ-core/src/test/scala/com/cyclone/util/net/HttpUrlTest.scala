package com.cyclone.util.net

import com.cyclone.util.SynchronizedMockeryComponent
import com.cyclone.util.net.HostAndPort.fromString
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}

class HttpUrlTest extends WordSpec with Matchers with ScalaFutures with IntegrationPatience {

  class Fixture extends TestDnslookupComponent with SynchronizedMockeryComponent {

    implicit val dns: DnsLookup = dnsLookup

    val domain = "SOMEDOMAIN.COM"

    val address = "192.168.1.2"
    val host = "someHost"
    val hostFQDN = "someHost.someDomain.com"
  }

  "HttpUrl" when {
    "getting qualified host in domain" when {
      "domain passed" must {
        "look up FQDN when and address specified" in new Fixture {
          willLookupAddressAndPTRs(address, Seq(DnsRecord.PTR(hostFQDN)))

          HttpUrl
            .fromParts(fromString(address), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(Some(domain))
            .futureValue shouldBe HttpUrl.fromString(s"http://$hostFQDN/path")
        }

        "look up FQDN when unqualified host specified" in new Fixture {
          willLookupAddressAndPTRs(host, Seq(DnsRecord.PTR(hostFQDN)))

          HttpUrl
            .fromParts(fromString(host), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(Some(domain))
            .futureValue shouldBe HttpUrl.fromString(s"http://$hostFQDN/path")
        }

        "use existing FQDN when FQDN specified" in new Fixture {
          HttpUrl
            .fromParts(fromString(hostFQDN), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(Some(domain))
            .futureValue shouldBe HttpUrl.fromString(s"http://$hostFQDN/path")
        }

        "use first host matching domain if resolve to multiple hosts" in new Fixture {
          willLookupAddressAndPTRs(
            address,
            Seq(DnsRecord.PTR("otherHost1"), DnsRecord.PTR(hostFQDN), DnsRecord.PTR("otherHost2"))
          )

          HttpUrl
            .fromParts(fromString(address), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(Some(domain))
            .futureValue shouldBe HttpUrl.fromString(s"http://$hostFQDN/path")
        }
      }

      "no domain name passed" must {
        "use first resolved host" in new Fixture {
          willLookupAddressAndPTRs(
            address,
            Seq(DnsRecord.PTR("host1"), DnsRecord.PTR("host2"), DnsRecord.PTR("host3"))
          )

          HttpUrl
            .fromParts(fromString(address), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(None)
            .futureValue shouldBe HttpUrl.fromString("http://host1/path")
        }
      }

      "host lookup fails" must {
        "use the address" in new Fixture {
          willLookupAddressAndPTRs(address, Nil)

          HttpUrl
            .fromParts(fromString(address), "/path", ssl = false)
            .withQualifiedHostNameIfInDomain(Some(domain))
            .futureValue shouldBe HttpUrl.fromString(s"http://$address/path")
        }
      }
    }
  }
}
