package com.cyclone.util.kerberos

import java.nio.file.{Files, Path}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{ActorMaterializer, IOResult, Materializer}
import akka.util.ByteString
import com.cyclone.akka.MaterializerComponent
import com.cyclone.util.kerberos.settings.ArtifactDeploymentResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Deploys Kerberos artifacts and sets system properties etc.
  */
trait KerberosDeployer {
  /**
    * Deploys artifacts and returns the deployment result
    *
    * @param kerberosArtifacts the artifacts to deploy
    * @param deployLocation    location to deploy artifacts if supplied otherwise use random temporary files
    * @return the result of deployment
    */
  def deploy(
    kerberosArtifacts: KerberosArtifacts,
    deployLocation: Option[ArtifactDeploymentInfo] = None): Future[ArtifactDeploymentResult]
}

object KerberosDeployer {

  def create(implicit system: ActorSystem): KerberosDeployer = {
    val component = new DefaultKerberosDeployerComponent with MaterializerComponent {
      implicit def materializer: Materializer = ActorMaterializer()
    }

    component.kerberosDeployer
  }
}

trait KerberosDeployerComponent {
  def kerberosDeployer: KerberosDeployer
}

trait DefaultKerberosDeployerComponent extends KerberosDeployerComponent {
  self: MaterializerComponent =>

  private sealed trait DeploymentPath {
    def path: Path
  }

  private object DeploymentPath {

    def from(optPath: Option[Path], newPrefix: String): DeploymentPath =
      optPath match {
        case None       => NewTempFile(newPrefix)
        case Some(path) => ExistingFile(path)
      }

    case class NewTempFile(prefix: String) extends DeploymentPath {
      def path: Path = {
        val tempPath = Files.createTempFile(prefix, "conf")
        tempPath.toFile.deleteOnExit()

        tempPath
      }
    }

    case class ExistingFile(path: Path) extends DeploymentPath

  }

  lazy val kerberosDeployer: KerberosDeployer = new KerberosDeployer {

    def deploy(
      kerberosArtifacts: KerberosArtifacts,
      locations: Option[ArtifactDeploymentInfo]): Future[ArtifactDeploymentResult] = {

      // Otherwise will not callback to get credentials
      System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")

      for {
        kerb5Path <- deployAndSetProp(
          kerberosArtifacts.kerb5ConfContent,
          DeploymentPath.from(locations.map(_.kerb5ConfPath), "kerb5"),
          "java.security.krb5.conf")

        loginPath <- deployAndSetProp(
          kerberosArtifacts.loginConfContent,
          DeploymentPath.from(locations.map(_.loginConfPath), "login"),
          "java.security.auth.login.config")

        keyTabPath <- deployFile(
          kerberosArtifacts.keyTabContent,
          DeploymentPath.from(locations.map(_.keyTabPath), "keytab")
        )
      } yield
        ArtifactDeploymentResult(
          ArtifactDeploymentInfo(
            kerb5ConfPath = kerb5Path,
            loginConfPath = loginPath,
            kerberosArtifacts.servicePrincipalName,
            keyTabPath = keyTabPath))
    }

    private def deployFile(content: Source[ByteString, _], deploymentPath: DeploymentPath): Future[Path] = {
      val path = deploymentPath.path

      val result = content.runWith(FileIO.toPath(path))

      result.flatMap {
        case IOResult(_, Success(_)) => Future.successful(path)
        case IOResult(_, Failure(e)) => Future.failed(e)
      }
    }

    private def deployAndSetProp(
      content: Source[ByteString, NotUsed],
      deploymentPath: DeploymentPath,
      systemPropertyName: String): Future[Path] = {

      deployFile(content, deploymentPath)
        .map { path =>
          System.setProperty(systemPropertyName, path.toString)
          path
        }
    }
  }
}