package com.cyclone.ipmi.command.chassis

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.chassis.ChassisIdentify.IdentifyInterval.{Off, On, OnFor}
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

import scala.concurrent.duration.FiniteDuration

/**
  * Chassis Identify command and response
  */
object ChassisIdentify {

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult.type] = new Decoder[CommandResult.type] {
      def decode(data: ByteString): CommandResult.type = CommandResult
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult.type] = StatusCodeTranslator[CommandResult.type]()
  }


  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        interval match {
          case Off         =>
            b += 0
            b += false.toBit0
          case OnFor(time) =>
            b += time.toSeconds.toByte
            b += false.toBit0
          case On          =>
            b += 0
            b += true.toBit0
        }

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult.type] = CommandResultCodec.commandResultCodecFor[Command, CommandResult.type]

  }

  sealed trait IdentifyInterval

  object IdentifyInterval {

    case object Off extends IdentifyInterval

    case class OnFor(time: FiniteDuration) extends IdentifyInterval

    case object On extends IdentifyInterval

  }

  case class Command(interval: IdentifyInterval) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x04)
  }

}
