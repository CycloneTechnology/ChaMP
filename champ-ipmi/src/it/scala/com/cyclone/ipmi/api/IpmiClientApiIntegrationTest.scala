package com.cyclone.ipmi.api

import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import com.cyclone.akka.{ActorSystemComponent, ActorSystemShutdown}
import com.cyclone.command.{OperationDeadline, TimeoutContext}
import com.cyclone.ipmi._
import com.cyclone.ipmi.command.chassis.GetChassisStatus
import com.cyclone.ipmi.protocol._
import com.cyclone.ipmi.protocol.rakp.RmcpPlusAndRakpStatusCodeErrors
import org.scalatest.{Inside, Matchers, WordSpecLike}
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.duration._

/**
  * Integration test for the [[IpmiClient]] API
  */
class IpmiClientApiIntegrationTest
  extends BaseIntegrationTest
    with WordSpecLike
    with Matchers
    with Inside
    with ImplicitSender
    with ActorSystemShutdown {

  val versionRequirement = IpmiVersionRequirement.V20Only

  class Fixture
    extends ActorIpmiClientComponent
      with TestIpmiManagerComponent
      with ActorSystemComponent {
    implicit val actorSystem: ActorSystem = system

    val connection = ipmiClient.connectionFor(host, port).futureValue

    implicit val timeoutContext: TimeoutContext = TimeoutContext(deadline = OperationDeadline.fromNow(10.seconds))
  }

  "an ipmi api" must {
    "allow creating a session" in new Fixture {
      connection.negotiateSession(credentials, versionRequirement)
        .futureValue shouldBe ().right
    }

    "fail to create session e.g. when use wrong user name" in new Fixture {
      connection.negotiateSession(IpmiCredentials("ss", "ADMIN"), versionRequirement)
        .futureValue shouldBe RmcpPlusAndRakpStatusCodeErrors.InvalidIntegrityCheckValue.left
    }

    "execute a command" in new Fixture {
      val r = for {
        _ <- connection.negotiateSession(credentials, versionRequirement)
        result <- connection.executeCommandOrError(GetChassisStatus.Command)
      } yield result

      inside(r.futureValue) {
        case \/-(x) => x shouldBe a[GetChassisStatus.CommandResult]
      }
    }
  }

  "closes session" in new Fixture {
    connection.closedown().futureValue shouldBe()
  }
}

