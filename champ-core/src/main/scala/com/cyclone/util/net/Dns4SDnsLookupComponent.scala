package com.cyclone.util.net

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.LoggingReceive
import akka.io.IO
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.cyclone.akka.FirstResponseSender.Requests
import com.cyclone.akka.{ActorSystemComponent, FirstResponseSender}
import com.cyclone.util.net.DnsLookupActor._
import com.cyclone.util.{AbsoluteDeadline, OperationDeadline}
import com.github.mkroli.dns4s.akka.Dns
import com.google.common.net.InetAddresses

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Fully non-blocking [[DnsLookupComponent]] implementation that uses Dns4s and Akka.
  *
  * The underlying implementation is blocking.
  */
trait Dns4sDnsLookupComponent extends DnsLookupComponent {
  self: ActorSystemComponent with DnsConfigSourceComponent =>

  import akka.pattern.ask

  lazy val dnsLookup: DnsLookup = new DnsLookup {

    private val dnsLookupActor = actorSystem.actorOf(DnsLookupActor.props(dnsConfigSource))

    // Long timeout so that timeouts from config generally take effect
    implicit val timeout: Timeout = Timeout(1.minute)

    def lookupPTRs(hostAddress: String): Future[Seq[DnsRecord.PTR]] =
      (dnsLookupActor ? DnsLookupActor.LookupPTRs(hostAddress)).mapTo[Seq[DnsRecord.PTR]]

    def lookupAddressAndPTRs(hostOrAddress: String): Future[Seq[DnsRecord.PTR]] =
      (dnsLookupActor ? DnsLookupActor.LookupAddressAndPTRs(hostOrAddress))
        .mapTo[Seq[DnsRecord.PTR]]

    def lookupMXs(mailAddressDomain: String): Future[Seq[DnsRecord.MX]] =
      (dnsLookupActor ? DnsLookupActor.LookupMXs(mailAddressDomain)).mapTo[Seq[DnsRecord.MX]]
  }
}

private[net] object DnsLookupActor {

  def props(dnsConfigSource: DnsConfigSource): Props =
    Props(new DnsLookupActor(dnsConfigSource))

  sealed trait Command

  case class LookupPTRs(hostAddress: String) extends Command

  case class LookupAddressAndPTRs(hostOrAddress: String) extends Command

  case class LookupMXs(mailAddressDomain: String) extends Command

}

private[net] class DnsLookupActor(dnsConfigSource: DnsConfigSource) extends Actor {
  implicit val actorSystem: ActorSystem = context.system

  import com.github.mkroli.dns4s.Message
  import com.github.mkroli.dns4s.dsl._

  def receive: Receive = LoggingReceive {
    case cmd: Command =>
      cmd match {
        case LookupPTRs(host) =>
          val result = for {
            dnsConfig <- dnsConfigSource.dnsConfig
            deadline = OperationDeadline.fromNow(dnsConfig.timeout)
            result <- lookupPTRs(dnsConfig, host, deadline)
          } yield result

          result.pipeTo(sender())

        case LookupAddressAndPTRs(hostOrAddr) =>
          val result = for {
            dnsConfig <- dnsConfigSource.dnsConfig
            deadline = OperationDeadline.fromNow(dnsConfig.timeout)
            result <- lookupAddressAndPTRs(dnsConfig, hostOrAddr, deadline)
          } yield result

          result.pipeTo(sender())

        case LookupMXs(mailAddressDomain) =>
          val result = for {
            dnsConfig <- dnsConfigSource.dnsConfig
            deadline = OperationDeadline.fromNow(dnsConfig.timeout)
            result <- lookupMXs(dnsConfig, mailAddressDomain, deadline)
          } yield result

          result.pipeTo(sender())
      }
  }

  private def lookupPTRs(
    dnsConfig: DnsConfig,
    hostAddress: String,
    deadline: OperationDeadline
  ): Future[Seq[DnsRecord.PTR]] = {
    def arpaAddress(hostAddress: String) =
      hostAddress.split('.').reverse.mkString(".") + ".in-addr.arpa."

    performQuery(dnsConfig, Query ~ Questions(QName(arpaAddress(hostAddress)) ~ TypePTR), deadline)
      .map {
        case Response(Answers(answers)) =>
          answers.collect {
            case PTRRecord(resource) => DnsRecord.PTR.fromRaw(resource.ptrdname)
          }
      }
  }

  private def lookupAddressAndPTRs(
    dnsConfig: DnsConfig,
    hostOrAddress: String,
    deadline: OperationDeadline
  ): Future[Seq[DnsRecord.PTR]] = {
    for {
      address <- addressFor(dnsConfig, hostOrAddress, deadline)
      ptrs    <- lookupPTRs(dnsConfig, address, deadline)
    } yield ptrs
  }

  private def lookupAddresses(
    dnsConfig: DnsConfig,
    host: String,
    deadline: OperationDeadline
  ): Future[Seq[String]] = {
    def queryWithDomainPrefix(domainPrefix: String): Future[Seq[String]] =
      performQuery(dnsConfig, Query ~ Questions(QName(host + domainPrefix) ~ TypeA), deadline).map {
        case Response(Answers(answers)) =>
          answers.collect {
            case ARecord(resource) => resource.address.getHostAddress
          }
      }

    val first = Future
      .find(dnsConfig.domainNameSuffixes.toStream.map(queryWithDomainPrefix))(_.nonEmpty)

    first.map(_.getOrElse(Nil))
  }

  private def addressFor(
    dnsConfig: DnsConfig,
    hostOrAddress: String,
    deadline: OperationDeadline
  ) = {
    if (InetAddresses.isInetAddress(hostOrAddress))
      Future.successful(hostOrAddress)
    else
      lookupAddresses(dnsConfig, hostOrAddress, deadline).map(_.headOption.getOrElse(hostOrAddress))
  }

  private def lookupMXs(
    dnsConfig: DnsConfig,
    mailAddressDomain: String,
    deadline: OperationDeadline
  ): Future[Seq[DnsRecord.MX]] = {
    performQuery(dnsConfig, Query ~ Questions(QName(mailAddressDomain) ~ TypeMX), deadline).map {
      case Response(Answers(answers)) =>
        answers
          .collect {
            case MXRecord(resource) => DnsRecord.MX(resource.exchange, resource.preference)
          }
          .sortBy(mx => mx.mxLevel)
    }
  }

  private def performQuery(
    dnsConfig: DnsConfig,
    message: ComposableMessage,
    deadline: OperationDeadline
  ): Future[Message] = {
    implicit val timeout: Timeout = Timeout(dnsConfig.timeout)

    val requests = Requests(
      dnsConfig.dnsServerSocketAddresses.map { socketAddr =>
        IO(Dns) -> Dns.DnsPacket(message, socketAddr)
      }
    )

    (context.system.actorOf(FirstResponseSender.props[Message](deadline.timeRemaining)) ? requests)
      .mapTo[Message]
  }

}
