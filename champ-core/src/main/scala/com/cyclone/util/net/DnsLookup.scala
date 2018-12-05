package com.cyclone.util.net

import java.net.InetAddress
import java.util.Properties

import com.google.common.net.InetAddresses
import javax.naming.directory.{Attribute, InitialDirContext}
import javax.naming.{Context, NameNotFoundException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}

trait DnsLookup {
  /**
    * Looks up PTR (domain name) records for a host address
    */
  def lookupPTRs(hostAddress: String): Future[Seq[DnsRecord.PTR]]

  /**
    * Looks up PTR (domain name) records for a host name or address
    */
  def lookupAddressAndPTRs(hostOrAddress: String): Future[Seq[DnsRecord.PTR]]

  /**
    * Looks up MX (main exchange) records for an email address
    */
  def lookupMXs(mailAddressDomain: String): Future[Seq[DnsRecord.MX]]
}

trait DnsLookupComponent {
  def dnsLookup: DnsLookup
}

/**
  * [[DnsLookupComponent]] implementation that uses the Java naming API.
  *
  * The underlying implementation is blocking.
  */
trait JavaNamingDnsLookupComponent extends DnsLookupComponent {
  lazy val dnsLookup: DnsLookup = new DnsLookup {

    private lazy val idc = {
      val env = new Properties
      env.put(Context.INITIAL_CONTEXT_FACTORY,
        "com.sun.jndi.dns.DnsContextFactory")
      env.put("com.sun.jndi.ldap.read.timeout", "5000")

      new InitialDirContext(env)
    }

    private val ptr = "PTR"
    private val mx = "MX"

    def lookupPTRs(hostAddress: String): Future[Seq[DnsRecord.PTR]] = {
      def arpaAddress(hostAddress: String) = hostAddress.split('.').reverse.mkString(".") + ".in-addr.arpa."

      Future(blocking {
        try {
          queryRawRecords(arpaAddress(hostAddress), ptr)
            .map(DnsRecord.PTR.fromRaw)
        } catch {
          case _: NameNotFoundException => Nil
        }
      })
    }

    def lookupAddressAndPTRs(hostOrAddress: String): Future[Seq[DnsRecord.PTR]] = {
      for {
        address <- addressFor(hostOrAddress)
        ptrs <- lookupPTRs(address)
      } yield ptrs
    }

    def lookupMXs(mailAddressDomain: String): Future[Seq[DnsRecord.MX]] =
      Future(blocking {
        try {
          queryRawRecords(mailAddressDomain, mx).map(DnsRecord.MX.fromRaw).sortBy(mx => mx.mxLevel)
        } catch {
          case _: NameNotFoundException => Nil
        }
      })

    private def addressFor(hostOrAddress: String) = {
      if (InetAddresses.isInetAddress(hostOrAddress))
        Future.successful(hostOrAddress)
      else
        Future(blocking {
          InetAddress.getByName(hostOrAddress).getHostAddress
        })
    }

    private def queryRawRecords(input: String, attributeName: String): Seq[String] =
      for {
        a <- queryAttribute(input, attributeName).toSeq
        r <- attributeToList(a)
      } yield r

    private def queryAttribute(input: String, attributeName: String): Option[Attribute] =
      Option(idc.getAttributes(input, Array(attributeName)).get(attributeName))

    private def attributeToList(a: Attribute): Seq[String] =
      (0 until a.size())
        .map(a.get)
        .collect {
          case s: String => s
        }
  }

}


