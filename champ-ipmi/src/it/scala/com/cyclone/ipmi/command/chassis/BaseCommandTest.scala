package com.cyclone.ipmi.command.chassis
import com.cyclone.akka.{ActorSystemComponent, ActorSystemShutdown}
import com.cyclone.ipmi.client.ActorIpmiClientComponent
import com.cyclone.ipmi.protocol.TestIpmiManagerComponent
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.{BaseIntegrationTest, DefaultIpmiComponent}
import org.scalatest.{Inside, Matchers, WordSpecLike}

trait BaseCommandTest extends BaseIntegrationTest with WordSpecLike with Matchers with Inside with ActorSystemShutdown {

  class Fixture
      extends DefaultIpmiComponent
      with ActorIpmiClientComponent
      with TestIpmiManagerComponent
      with ActorSystemComponent {

    def actorSystem = system

    def executeCommand[C <: IpmiStandardCommand, R <: IpmiCommandResult](command: C)(
      implicit codec: CommandResultCodec[C, R]
    ): R = ipmi.executeCommand(target, command).futureValue
  }

}
