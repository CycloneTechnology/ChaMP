package com.cyclone.util.kerberos

import java.io.{File, IOException}

import akka.stream.scaladsl.Source
import akka.testkit.ImplicitSender
import akka.util.ByteString
import com.cyclone.akka.{ActorSystemShutdown, TestKitSupport}
import com.cyclone.util.SynchronizedMockeryComponent
import com.cyclone.util.kerberos.settings.{ArtifactDeploymentResult, KerberosDeploymentSettings, KerberosDeploymentSettingsComponent}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Inside, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.control.NoStackTrace

class KerberosDeploymentActorTest
  extends TestKitSupport
    with WordSpecLike
    with Matchers
    with ActorSystemShutdown
    with ScalaFutures
    with ImplicitSender
    with Inside {

  class Fixture
    extends DefaultKerberosDeploymentActorComponent
      with MockKerberosArtifactsSourceComponent
      with MockKerberosDeployerComponent
      with SynchronizedMockeryComponent
      with TestActorSystemComponent
      with KerberosDeploymentSettingsComponent {

    def kerberosDeploymentSettings = KerberosDeploymentSettings(
      retryDelay = 1.second
    )

    // WLOG
    val kerberosArtifacts = KerberosArtifacts(Source.empty, Source.empty, "", Source.empty)

    // WLOG - but different from above
    val kerberosArtifacts2 = KerberosArtifacts(Source.single(ByteString.empty), Source.empty, "", Source.empty)

    val info = ArtifactDeploymentInfo(
      new File("kerb5").toPath,
      new File("login").toPath,
      "",
      new File("keytab").toPath
    )
    val result = ArtifactDeploymentResult(info)

    def awaitState[U](pf: PartialFunction[KerberosDeploymentActor.State, U]) =
      awaitAssert {
        kerberosDeploymentActor ! KerberosDeploymentActor.GetState
        inside(expectMsgType[KerberosDeploymentActor.State])(pf)
      }
  }

  "KerberosDeploymentActor" must {
    "work" in new Fixture {
      willGetKerberosArtifacts(kerberosArtifacts)
      willDeployKerberosArtifacts(kerberosArtifacts, None, result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      kerberosDeploymentActor ! KerberosDeploymentActor.GetDeploymentInfo
      expectMsg(info)
    }

    "retry on artifact source failure" in new Fixture {
      willFailToGetKerberosArtifacts(new IOException with NoStackTrace)

      willGetKerberosArtifacts(kerberosArtifacts)
      willDeployKerberosArtifacts(kerberosArtifacts, None, result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      kerberosDeploymentActor ! KerberosDeploymentActor.GetDeploymentInfo
      expectMsg(info)
    }

    "retry on deployment failure" in new Fixture {
      willGetKerberosArtifacts(kerberosArtifacts)
      willFailToDeployKerberosArtifacts(kerberosArtifacts, None, new IOException with NoStackTrace)

      willGetKerberosArtifacts(kerberosArtifacts)
      willDeployKerberosArtifacts(kerberosArtifacts, None, result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      kerberosDeploymentActor ! KerberosDeploymentActor.GetDeploymentInfo
      expectMsg(info)
    }

    "redeploy when requested" in new Fixture {
      willGetKerberosArtifacts(kerberosArtifacts)
      willDeployKerberosArtifacts(kerberosArtifacts, None, result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      kerberosDeploymentActor ! KerberosDeploymentActor.GetDeploymentInfo
      expectMsg(info)

      willGetKerberosArtifacts(kerberosArtifacts2)
      willDeployKerberosArtifacts(kerberosArtifacts2, Some(result.information), result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      awaitState {
        case KerberosDeploymentActor.State(numDeps, _, _) => numDeps shouldBe 2
      }
    }

    "abandon retry when asked to redeploy" in new Fixture {
      override def kerberosDeploymentSettings = KerberosDeploymentSettings(
        retryDelay = 10.second
      )

      willGetKerberosArtifacts(kerberosArtifacts)
      willFailToDeployKerberosArtifacts(kerberosArtifacts, None, new IOException with NoStackTrace)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      awaitState {
        case KerberosDeploymentActor.State(_, inProgress, _) => inProgress shouldBe true
      }

      willGetKerberosArtifacts(kerberosArtifacts2)
      willDeployKerberosArtifacts(kerberosArtifacts2, None, result)

      kerberosDeploymentActor ! KerberosDeploymentActor.Deploy

      awaitState {
        case KerberosDeploymentActor.State(numDeps, inProgress, _) =>
          inProgress shouldBe false
          numDeps shouldBe 1
      }
    }
  }
}
