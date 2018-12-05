package com.cyclone.akka

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestKit
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.ClassTag

object ActorSystemSupport {

  def effectiveConfig(configSource: String, baseConfig: Config): Config =
    ConfigFactory.parseString(configSource).withFallback(baseConfig)

  val clusterConfig =
    """
      |akka.actor.provider=akka.cluster.ClusterActorRefProvider
      |akka.remote.netty.tcp.port=0
    """.stripMargin

  val nonClusterConfig = ""
}

import com.cyclone.akka.ActorSystemSupport._

abstract class TestKitSupportBase(
  actorSystemName: String = "TestClusterActorSystem",
  configSource: String,
  baseConfig: Config = ConfigFactory.load()
) extends TestKit(ActorSystem(actorSystemName, effectiveConfig(configSource, baseConfig)))
    with ActorSystemComponent {
  self: Suite with ActorSystemShutdown =>

  lazy val actorSystem = system

  implicit def executor: ExecutionContextExecutor = actorSystem.dispatcher

  // Expose an TestActorSystemComponent that inner classes of the test can use to mix in if they need to...
  trait TestActorSystemComponent extends ActorSystemComponent {
    lazy val actorSystem = self.system
  }

}

/**
  * To aid testing with Akka.
  */
abstract class TestKitSupport(
  actorSystemName: String = "TestActorSystem",
  configSource: String = nonClusterConfig,
  baseConfig: Config = ConfigFactory.load()
) extends TestKitSupportBase(actorSystemName, configSource, baseConfig) {
  self: Suite with ActorSystemShutdown =>

  def subscribeMessages[T: ClassTag](subscriber: ActorRef) =
    system.eventStream.subscribe(subscriber, implicitly[ClassTag[T]].runtimeClass)
}

trait ActorSystemShutdown extends BeforeAndAfterAll {
  self: Suite with ActorSystemComponent =>

  override def afterAll() {
    super.afterAll()
    TestKit.shutdownActorSystem(actorSystem)
  }
}
