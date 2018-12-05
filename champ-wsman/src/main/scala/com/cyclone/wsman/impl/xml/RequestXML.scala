package com.cyclone.wsman.impl.xml

import com.cyclone.command.SelectorClause
import com.cyclone.util.OperationDeadline

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.xml.Elem

object RequestXML {
  def uuid: String = "uuid:" + java.util.UUID.randomUUID().toString.toUpperCase()

  val minTimeout: FiniteDuration = 1 seconds
  val maxEnvelopeSize = 153600

  def selectorSetElt(selectorClause: SelectorClause): Elem =
    if (selectorClause.isEmpty) {
      <w:SelectorSet/>
    } else {
      // @formatter:off
      <w:SelectorSet>{for (
        selector <- selectorClause.selectors
      ) yield
        <w:Selector Name={selector.name}>{selector.value}</w:Selector>}
      </w:SelectorSet>
      // @formatter:on
    }
}

trait RequestXML {

  import RequestXML._

  def xml(toAddress: String): Elem

  // @formatter:off
  val maxEnvelopeSizeElt: Elem = <w:MaxEnvelopeSize s:mustUnderstand="true">{maxEnvelopeSize}</w:MaxEnvelopeSize>
  // @formatter:on

  def optionalDeadline: Option[OperationDeadline]

  def optionalTimeRemaining: Option[FiniteDuration] =
    optionalDeadline map (_.timeRemaining max minTimeout)

  // @formatter:off
  protected def timeoutElt: Elem =
    <w:OperationTimeout>PT{optionalTimeRemaining.getOrElse(minTimeout).toSeconds}S</w:OperationTimeout>
  // @formatter:on
}

trait RequestXMLWithNoDeadline extends RequestXML {
  val optionalDeadline: Option[OperationDeadline] = None
}

trait RequestXMLWithDeadline extends RequestXML {
  def deadline: OperationDeadline

  lazy val optionalDeadline = Some(deadline)
}
