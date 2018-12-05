package com.cyclone.ipmi.protocol.security

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.typesafe.scalalogging.LazyLogging

object CipherSuite extends LazyLogging {
  // Does not include suites we don't support
  def decode(data: ByteString): List[CipherSuite] = {

    val rawRecords = data.splitWhenever(b => b == 0xC0.toByte || b == 0xC1.toByte)

    val suites = rawRecords.flatMap { raw =>
      val iterator = raw.iterator
      val is = iterator.asInputStream

      // Ignore suite Id
      if (raw.head == 0xC0.toByte)
        is.skip(2)
      else
        is.skip(5)

      for {
        authAlg <- AuthenticationAlgorithm.fromCode(is.readByte.bits0To5)

        integAlgId <- iterator.find(b => b.bit6).map(_.bits0To5)
        integAlg <- IntegrityAlgorithm.fromCode(integAlgId)

        confAlgId <- iterator.find(b => b.bit7).map(_.bits0To5)
        confAlg <- ConfidentialityAlgorithm.fromCode(confAlgId)
      } yield {
        CipherSuite(authAlg, confAlg, integAlg)
      }
    }

    suites
  }

  implicit val ordering: Ordering[CipherSuite] =
    Ordering.by {
      cs: CipherSuite => (cs.authenticationAlgorithm, cs.confidentialityAlgorithm, cs.integrityAlgorithm)
    }

  def bestOf(cipherSuites: Seq[CipherSuite]): Option[CipherSuite] = {
    val best = cipherSuites.sorted.lastOption

    logger.debug(s"Choosing $best as best cipher suite from $cipherSuites")

    best
  }
}

/**
  * Suite of cipher algorithms
  */
case class CipherSuite(
  authenticationAlgorithm: AuthenticationAlgorithm,
  confidentialityAlgorithm: ConfidentialityAlgorithm,
  integrityAlgorithm: IntegrityAlgorithm
)
