package com.cyclone.ipmi.tool.command

import com.cyclone.akka.{ActorSystemComponent, ActorSystemShutdown}
import com.cyclone.ipmi.{DefaultIpmiComponent, _}
import com.cyclone.ipmi.api.ActorIpmiClientComponent
import com.cyclone.ipmi.protocol.TestIpmiManagerComponent
import com.cyclone.ipmi.tool.command.IpmiCommands._
import org.scalatest.{Matchers, Suite}
import scalaz.{-\/, \/-}

/**
  * Base class for testing tool commands
  */
abstract class BaseToolCommandTest
  extends BaseIntegrationTest {
  self: Matchers with Suite with ActorSystemShutdown =>

  class Fixture extends DefaultIpmiComponent
    with ActorIpmiClientComponent
    with TestIpmiManagerComponent
    with ActorSystemComponent {

    def actorSystem = system

    def executeCommand[C <: IpmiToolCommand, R <: IpmiToolCommandResult](command: C)(implicit executor: CommandExecutor[C, R]): R = {
      val resultOrError = ipmi.executeToolCommandOrError(target, command).futureValue

      resultOrError match {
        case \/-(result) => result.asInstanceOf[R]
        case -\/(e)      => fail(s"expected command success was $e")
      }
    }
  }

}
