package com.cyclone.util.net
import com.cyclone.util.MockeryComponent
import org.jmock.AbstractExpectations._
import org.jmock.Expectations

import scala.concurrent.Future

trait TestDnslookupComponent extends DnsLookupComponent {
  self: MockeryComponent =>

  lazy val dnsLookup: DnsLookup = mockery.mock(classOf[DnsLookup])

  def willLookupAddressAndPTRs(address: String, result: Seq[DnsRecord.PTR]): Unit =
    mockery.checking(new Expectations {
      oneOf(dnsLookup).lookupAddressAndPTRs(address)
      will(returnValue(Future.successful(result)))
    })

}
