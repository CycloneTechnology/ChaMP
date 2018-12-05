package com.cyclone.ipmi.protocol

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.IpmiCredentials
import com.cyclone.ipmi.protocol.SessionContext.{ReqHashMaker, ResHashMaker}
import com.cyclone.ipmi.protocol.packet.SessionId.ManagedSystemSessionId
import com.cyclone.ipmi.protocol.packet.SessionSequenceNumber
import com.cyclone.ipmi.protocol.security._

// TODO code for auth/encryption is a little bit spread between here
// and the IpmiXXSessionWrapper classes - and not as easy to follow
// as it should ideally be. Can we improve this?
sealed trait SessionContext {

  def requireResponseAuthCheck(responseAuthenticationType: AuthenticationType): Boolean

  def initialSendSequenceNumber: SessionSequenceNumber

  def managedSystemSessionId: ManagedSystemSessionId

  def cipherSuite: CipherSuite

  def authenticationType: AuthenticationType

  def payloadEncrypted: Boolean = cipherSuite.confidentialityAlgorithm != ConfidentialityAlgorithm.Plain

  def payloadAuthenticated: Boolean = cipherSuite.authenticationAlgorithm != AuthenticationAlgorithm.NoAuth

  def encryptor: SessionContext.Encryptor

  def decryptor: SessionContext.Decryptor

  def responseHashMaker: SessionContext.ResHashMaker

  def requestHashMaker: SessionContext.ReqHashMaker
}

object SessionContext {
  type Encryptor = ByteString => ByteString
  type Decryptor = ByteString => ByteString
  type ReqHashMaker = (ManagedSystemSessionId, SessionSequenceNumber, ByteString) => ByteString
  type ResHashMaker = (ManagedSystemSessionId, SessionSequenceNumber, ByteString) => Option[ByteString]

  val ResNoHash: ResHashMaker = {
    case (_, _, _) => None
  }

  val ReqNoHash: ReqHashMaker = {
    case (_, _, _) => ByteString.empty
  }

  case object NoSession extends SessionContext {
    val managedSystemSessionId = ManagedSystemSessionId(0)
    val initialSendSequenceNumber = SessionSequenceNumber(0)
    val authenticationType: AuthenticationType = AuthenticationType.NoAuth

    def requireResponseAuthCheck(responseAuthenticationType: AuthenticationType) = true

    val cipherSuite = CipherSuite(
      AuthenticationAlgorithm.NoAuth,
      ConfidentialityAlgorithm.Plain,
      IntegrityAlgorithm.Open
    )

    val encryptor: ByteString => ByteString = identity[ByteString]
    val decryptor: ByteString => ByteString = identity[ByteString]
    val responseHashMaker: ResHashMaker = ResNoHash
    val requestHashMaker: ReqHashMaker = ReqNoHash
  }

}

case class V15SessionContext(
  managedSystemSessionId: ManagedSystemSessionId,
  optCredentials: Option[IpmiCredentials] = None,
  authenticationType: AuthenticationType = AuthenticationType.NoAuth,
  sessionEstablished: Boolean = false
) extends SessionContext {
  val initialSendSequenceNumber = SessionSequenceNumber(1)

  val cipherSuite = CipherSuite(
    AuthenticationAlgorithm.NoAuth,
    ConfidentialityAlgorithm.Plain,
    IntegrityAlgorithm.Open
  )

  def requireResponseAuthCheck(responseAuthenticationType: AuthenticationType): Boolean =
  // Some BMCs do not send auth responses once the session is established.
  // Don't require checking if session established...
    if (!sessionEstablished)
      true
    else
      responseAuthenticationType != AuthenticationType.NoAuth

  val encryptor: ByteString => ByteString = identity[ByteString]
  val decryptor: ByteString => ByteString = identity[ByteString]

  val responseHashMaker: ResHashMaker = {
    case (sessionId, sequenceNumber, payload) =>
      optCredentials.map { credentials =>
        authenticationType.generateAuthCode(credentials.password, sessionId, sequenceNumber, payload)
      }
  }

  val requestHashMaker: ReqHashMaker = {
    case (sessionId, sequenceNumber, payload) =>
      responseHashMaker(sessionId, sequenceNumber, payload).getOrElse(ByteString.empty)
  }
}

case class V20SessionContext(
  managedSystemSessionId: ManagedSystemSessionId,
  cipherSuite: CipherSuite,
  optSik: Option[Key.SIK] = None) extends SessionContext {

  def requireResponseAuthCheck(responseAuthenticationType: AuthenticationType) = true

  val initialSendSequenceNumber = SessionSequenceNumber(1)

  val authenticationType: AuthenticationType = AuthenticationType.NoAuth

  val encryptor: ByteString => ByteString = { data: ByteString =>
    optSik.map { sik =>
      cipherSuite.confidentialityAlgorithm.encrypt(sik, data)
    }.getOrElse(data)
  }

  val decryptor: ByteString => ByteString = { data: ByteString =>
    optSik.map { sik =>
      cipherSuite.confidentialityAlgorithm.decrypt(sik, data)
    }.getOrElse(data)
  }

  val responseHashMaker: ResHashMaker = {
    case (_, _, data) =>
      optSik.map { sik =>
        cipherSuite.integrityAlgorithm.generateIntegrityCode(sik, data)
      }
  }

  val requestHashMaker: ReqHashMaker = {
    case (_, _, data) =>
      optSik.map { sik =>
        val integrityAlgorithm = cipherSuite.integrityAlgorithm

        val b = new ByteStringBuilder

        b ++= integrityAlgorithm.integrityPadding(data)
        b ++= integrityAlgorithm.generateIntegrityCode(sik, data)

        b.result()
      }.getOrElse(ByteString.empty)
  }
}