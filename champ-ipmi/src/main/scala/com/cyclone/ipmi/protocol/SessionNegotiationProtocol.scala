package com.cyclone.ipmi.protocol

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.{IpmiErrorOr, StatusCodeErrorOr}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command._
import com.cyclone.ipmi.command.ipmiMessagingSupport.{
  ActivateSession,
  GetChannelAuthenticationCapabilities,
  GetChannelCipherSuites,
  GetSessionChallenge
}
import com.cyclone.ipmi.protocol.packet.IpmiVersion
import com.cyclone.ipmi.protocol.packet.SessionId.{ManagedSystemSessionId, RemoteConsoleSessionId}
import com.cyclone.ipmi.protocol.rakp.{OpenSession, Rakp1_2, Rakp3_4, RmcpPlusAndRakpStatusCodeErrors}
import com.cyclone.ipmi.protocol.security._
import com.cyclone.ipmi._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._

/**
  * Performs message exchanges for session activation for a specific version or protocol.
  */
trait SessionNegotiationProtocol {
  def remoteConsoleSessionId: RemoteConsoleSessionId

  def credentials: IpmiCredentials

  def privilegeLevel: PrivilegeLevel

  def requester: Requester

  /**
    * Negotiates a session
    */
  def negotiateSession(): Future[IpmiErrorOr[SessionContext]]
}

object SessionNegotiationProtocol {

  case class V20(
    remoteConsoleSessionId: RemoteConsoleSessionId,
    credentials: IpmiCredentials,
    privilegeLevel: PrivilegeLevel,
    requester: Requester
  )(implicit timeoutContext: TimeoutContext)
      extends SessionNegotiationProtocol {

    val kuid: Key.UID = Key.UID.fromCredentials(credentials)
    val kg: Key.KG = Key.KG.fromCredentials(credentials)
    val version: IpmiVersion = IpmiVersion.V20

    val username: Username = credentials.username

    def negotiateSession(): Future[IpmiError \/ V20SessionContext] = {
      val result = for {
        cipherSuite <- eitherT(bestChannelCipherSuite)
        openSessionResult <- eitherT(
          openSession(remoteConsoleSessionId, cipherSuite, privilegeLevel)
        )
        sik <- doRakpExchanges(openSessionResult.managedSystemSessionId, cipherSuite)
      } yield V20SessionContext(openSessionResult.managedSystemSessionId, cipherSuite, Some(sik))

      result.run
    }

    private def bestChannelCipherSuite: Future[IpmiErrorOr[CipherSuite]] = {
      def cipherSuiteBytesFor(res: GetChannelCipherSuites.CommandResult): (Seq[Byte], Boolean) = {
        val bytes = res.cipherSuitesData
        (bytes, res.last)
      }

      val futSuites = (0 to 0x3f)
        .foldLeft(Future.successful((Vector.empty[Byte], false).right[IpmiError])) {
          case (f, index) =>
            f.flatMap {
              case \/-((acc, done)) =>
                if (!done)
                  requester.makeRequest(GetChannelCipherSuites.Command(index), IpmiVersion.V15).map {
                    case \/-(res) =>
                      val (bytes, newDone) = cipherSuiteBytesFor(res)
                      (acc ++ bytes, newDone).right

                    case -\/(e) => e.left
                  } else
                  Future.successful((acc, done).right)

              case -\/(e) => Future.successful(e.left)
            }
        }
        .map {
          case \/-((data, _)) => CipherSuite.decode(ByteString(data: _*)).right
          case -\/(e)         => e.left
        }

      futSuites.map {
        case \/-(suites) => CipherSuite.bestOf(suites).toRightDisjunction(NoSupportedCipherSuites)
        case -\/(e)      => e.left
      }
    }

    private def openSession(
      remoteConsoleSessionId: RemoteConsoleSessionId,
      cipherSuite: CipherSuite,
      privilegeLevel: PrivilegeLevel
    ) =
      requester.makeRequest(
        OpenSession.Command(remoteConsoleSessionId, cipherSuite, privilegeLevel),
        version
      )

    private def getAuthenticationCapabilities(requestedPrivilegeLevel: PrivilegeLevel) =
      requester.makeRequest(
        GetChannelAuthenticationCapabilities.Command(requestedPrivilegeLevel),
        version
      )

    private def doRakpExchanges(
      managedSystemSessionId: ManagedSystemSessionId,
      cipherSuite: CipherSuite
    ) = {
      val rakp1 = Rakp1_2.Command(
        managedSystemSessionId,
        Randomizer.randomBytes(16),
        privilegeLevel,
        username
      )

      for {
        rakp2Result <- eitherT(sendRacp1(rakp1))
        rakp4Result <- eitherT(sendRacp3(rakp1, rakp2Result, cipherSuite))
        sik = calulateSik(rakp1, rakp2Result, cipherSuite)
        _ <- eitherT(
          validateRakp4Response(rakp1, rakp2Result, rakp4Result, cipherSuite, sik).point[Future]
        )
      } yield sik
    }

    private def sendRacp1(rakp1: Rakp1_2.Command): Future[IpmiErrorOr[Rakp1_2.CommandResult]] =
      requester.makeRequest(rakp1, version)

    private def sendRacp3(
      rakp1: Rakp1_2.Command,
      rakp2Result: Rakp1_2.CommandResult,
      cipherSuite: CipherSuite
    ): Future[IpmiErrorOr[Rakp3_4.CommandResult]] = {

      val keyExchangeAuthCode = {
        // See spec section 13.31
        val b = new ByteStringBuilder
        b ++= rakp2Result.managedSystemRandomNumber
        b ++= rakp2Result.remoteConsoleSessionId.toBin

        b += rakp1.requestedMaximumPrivilegeLevel.toByte.set4

        b ++= username.toBin

        cipherSuite.authenticationAlgorithm.determineAuthCode(kuid, b.result())
      }

      val rakp2Validation = validateRakp2Response(rakp1, rakp2Result, cipherSuite)

      val statusCode = rakp2Validation.leftMap(_.code).swap.getOrElse(StatusCode.NoErrors)

      val msg = Rakp3_4.Command(statusCode, rakp1.managedSystemSessionId, keyExchangeAuthCode)

      val sendResult = requester.makeRequest(msg, version)

      // According to the spec, we are supposed to send back the status code from the
      // RAKP2 validation - but in error cases it says we will not receive a Rakp4
      // message in response (sec 13.22).
      // (Although in practice do seem to get a device specific status code response for some BMCs).
      // So do not wait for the response in error cases but immediately
      // return the error code in the validation...
      rakp2Validation match {
        case -\/(e) => e.left.point[Future]
        case \/-(_) => sendResult
      }
    }

    private def validateRakp2Response(
      rakp1: Rakp1_2.Command,
      rakp2Result: Rakp1_2.CommandResult,
      cipherSuite: CipherSuite
    ): StatusCodeErrorOr[Unit] = {
      if (rakp2Result.remoteConsoleSessionId != remoteConsoleSessionId)
        RmcpPlusAndRakpStatusCodeErrors.InvalidSessionId.left
      else {
        // See spec section 13.31
        val b = new ByteStringBuilder
        b ++= rakp2Result.remoteConsoleSessionId.toBin
        b ++= rakp1.managedSystemSessionId.toBin
        b ++= rakp1.consoleRandomNumber
        b ++= rakp2Result.managedSystemRandomNumber
        b ++= rakp2Result.managedSystemGuid

        b += rakp1.requestedMaximumPrivilegeLevel.toByte.set4

        b ++= username.toBin

        val expectedKeyExchangeAuthCode =
          cipherSuite.authenticationAlgorithm.determineAuthCode(kuid, b.result())

        if (expectedKeyExchangeAuthCode != rakp2Result.keyExchangeAuthCode)
          RmcpPlusAndRakpStatusCodeErrors.InvalidIntegrityCheckValue.left
        else
          ().right
      }
    }

    private def calulateSik(
      rakp1: Rakp1_2.Command,
      rakp2Result: Rakp1_2.CommandResult,
      cipherSuite: CipherSuite
    ) = {
      def sikBase() = {
        val b = new ByteStringBuilder
        b ++= rakp1.consoleRandomNumber
        b ++= rakp2Result.managedSystemRandomNumber
        b += rakp1.requestedMaximumPrivilegeLevel.toByte.set4
        b ++= username.toBin

        b.result()
      }

      cipherSuite.authenticationAlgorithm.determineSik(kg, sikBase())
    }

    private def validateRakp4Response(
      rakp1: Rakp1_2.Command,
      rakp2Result: Rakp1_2.CommandResult,
      rakp4Result: Rakp3_4.CommandResult,
      cipherSuite: CipherSuite,
      sik: Key.SIK
    ): IpmiErrorOr[Unit] = {

      if (rakp4Result.remoteConsoleSessionId != remoteConsoleSessionId)
        RmcpPlusAndRakpStatusCodeErrors.InvalidSessionId.left
      else {
        // See spec section 13.31
        val b = new ByteStringBuilder
        b ++= rakp1.consoleRandomNumber
        b ++= rakp1.managedSystemSessionId.toBin
        b ++= rakp2Result.managedSystemGuid

        val expectedKeyExchangeAuthCode =
          cipherSuite.authenticationAlgorithm.determineRakp4AuthCode(sik, b.result())

        if (expectedKeyExchangeAuthCode != rakp4Result.integrityCode)
          RmcpPlusAndRakpStatusCodeErrors.InvalidIntegrityCheckValue.left
        else
          ().right
      }
    }
  }

  case class V15(
    remoteConsoleSessionId: RemoteConsoleSessionId,
    credentials: IpmiCredentials,
    privilegeLevel: PrivilegeLevel,
    authenticationTypes: AuthenticationTypes,
    requester: Requester
  )(implicit timeoutContext: TimeoutContext)
      extends SessionNegotiationProtocol {

    def negotiateSession(): Future[IpmiError \/ V15SessionContext] = {
      AuthenticationType.mostSecureOf(authenticationTypes.types) match {
        case Some(authType) =>
          val result = for {
            challenge <- eitherT(
              requester.makeRequest(
                GetSessionChallenge.Command(authType, credentials.usernameV15),
                IpmiVersion.V15
              )
            )

            activation <- eitherT(
              requester.makeRequest(
                ActivateSession.Command(authType, privilegeLevel, challenge.challengeData),
                IpmiVersion.V15,
                V15SessionContext(
                  challenge.managedSystemSessionId,
                  Some(credentials),
                  authType,
                  sessionEstablished = false
                )
              )
            )
          } yield
            V15SessionContext(
              activation.managedSystemSessionId,
              Some(credentials),
              authType,
              sessionEstablished = true
            )

          result.run

        case None =>
          Future.successful(NoSupportedAuthenticationTypes(authenticationTypes).left)
      }
    }
  }

}
