package com.cyclone.util.net

import java.net.InetSocketAddress

import com.cyclone.util.ConfigUtils._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

case class DnsConfig(dnsServers: Seq[String], domainNames: Seq[String], timeout: FiniteDuration) {

  def dnsServerSocketAddresses: Seq[InetSocketAddress] =
    dnsServers.map { server =>
      val hap = HostAndPort.fromString(server)
      new InetSocketAddress(hap.host, hap.getPortOrDefault(53))
    }

  def domainNameSuffixes: Seq[String] = "" +: domainNames.map(name => "." + name)
}

trait DnsConfigSource {
  def dnsConfig: Future[DnsConfig]
}

trait DnsConfigSourceComponent {
  def dnsConfigSource: DnsConfigSource
}

trait ConfigDnsConfigSourceComponent extends DnsConfigSourceComponent {
  private lazy val config = ConfigFactory.load()

  private lazy val fdnsConfig = {
    val dnsConfig = DnsConfig(
      config.getStringList("cyclone.dns.servers").asScala,
      config.getStringList("cyclone.dns.domainNames").asScala,
      config.finiteDuration("cyclone.dns.timeout")
    )

    Future.successful(dnsConfig)
  }

  lazy val dnsConfigSource: DnsConfigSource = new DnsConfigSource {
    def dnsConfig: Future[DnsConfig] = fdnsConfig
  }
}
