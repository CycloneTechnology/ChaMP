package com.cyclone.ipmi.command.chassis

import java.util.concurrent.TimeUnit

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

import scala.concurrent.duration.FiniteDuration

/**
  * Get POH (Power On Hours) Counter command and response
  */
object GetPohCounter {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        def minutesPerCount = is.read(2).as[Short]
        def counterReading = is.read(4).as[Int]

        val powerOnTime = new FiniteDuration(minutesPerCount * counterReading, TimeUnit.MINUTES)

        CommandResult(powerOnTime)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(powerOnTime: FiniteDuration) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] = CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ChassisRequest
    val commandCode = CommandCode(0x0F)
  }

}
