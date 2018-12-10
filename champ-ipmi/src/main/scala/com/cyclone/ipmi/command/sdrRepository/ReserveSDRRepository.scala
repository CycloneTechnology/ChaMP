package com.cyclone.ipmi.command.sdrRepository

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}
import com.cyclone.ipmi.sdr.SdrReservationId

/**
  * Reserve SDR Repository command and response
  */
object ReserveSDRRepository {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString) = CommandResult(data.take(2).as[SdrReservationId])
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(reservationId: SdrReservationId) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x22)
  }

}
