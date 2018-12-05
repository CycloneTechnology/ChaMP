package com.cyclone.util.net

import com.cyclone.util.net.HostAndPort.fromString
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}

class HttpUrlTest extends WordSpec with Matchers with ScalaFutures with IntegrationPatience {

  implicit val dnsLookup: DnsLookup = new JavaNamingDnsLookupComponent {}.dnsLookup

  private val domain = "CYCLONE-TECHNOLOGY.COM"

  private val domain1Address = "10.0.0.4"
  private val domain1Host = "domain-1"
  private val domain1FQDN = "domain-1.cyclone-technology.com"

  "HttpUrl" when {
    "getting qualified host in domain" must {
      "look up FQDN when domain and address specified" in {
        HttpUrl
          .fromParts(fromString(domain1Address), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(Some(domain))
          .futureValue shouldBe
        HttpUrl.fromString(s"http://$domain1FQDN/path")
      }

      "look up FQDN when domain and unqualified host specified" in {
        HttpUrl
          .fromParts(fromString(domain1Host), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(Some(domain))
          .futureValue shouldBe
        HttpUrl.fromString(s"http://$domain1FQDN/path")
      }

      "use existing FQDN when domain and FQDN specified" in {
        HttpUrl
          .fromParts(fromString(domain1FQDN), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(Some(domain))
          .futureValue shouldBe
        HttpUrl.fromString(s"http://$domain1FQDN/path")
      }

      "use specified host if lookup fails" in {
        HttpUrl
          .fromParts(fromString("someHost"), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(Some(domain))
          .futureValue shouldBe
        HttpUrl.fromString("http://someHost/path")
      }

      "use host matching domain when address resolved to multiple host names" in {
        HttpUrl
          .fromParts(fromString("10.0.0.6"), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(Some(domain))
          .futureValue shouldBe
        HttpUrl.fromString("http://exchange.cyclone-technology.com/path")
      }

      "use first host unqualified resolves to multiple host names" in {
        HttpUrl
          .fromParts(fromString("mail"), "/path", ssl = false)
          .withQualifiedHostNameIfInDomain(None)
          .futureValue should
        (be(HttpUrl.fromString("http://mail/path")) or
        be(HttpUrl.fromString("http://exchange.cyclone-technology.com/path")))
      }
    }
  }
}
