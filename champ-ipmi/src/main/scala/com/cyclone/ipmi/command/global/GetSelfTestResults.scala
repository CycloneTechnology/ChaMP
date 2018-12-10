package com.cyclone.ipmi.command.global

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * Get Self Test Results command and response
  */
object GetSelfTestResults {

  sealed trait SelfTestStatus

  case object NoError extends SelfTestStatus

  case object NotImplemented extends SelfTestStatus

  object Corrupted {
    implicit val decoder: Decoder[Corrupted] = new Decoder[Corrupted] {

      def decode(data: ByteString): Corrupted = {
        val byte = data(0)

        Corrupted(
          cannotAccessSelDevice = byte.bit7,
          cannotAccessSdrRepository = byte.bit6,
          cannotAccessFruDevice = byte.bit5,
          ipmbSignalLinesDoNotRespond = byte.bit4,
          sdrRepositoryEmpty = byte.bit3,
          corruptIntenalUseFruArea = byte.bit2,
          corruptBootBlockFirmware = byte.bit1,
          corruptOperationFirmware = byte.bit0
        )
      }
    }
  }

  case class Corrupted(
    cannotAccessSelDevice: Boolean,
    cannotAccessSdrRepository: Boolean,
    cannotAccessFruDevice: Boolean,
    ipmbSignalLinesDoNotRespond: Boolean,
    sdrRepositoryEmpty: Boolean,
    corruptIntenalUseFruArea: Boolean,
    corruptBootBlockFirmware: Boolean,
    corruptOperationFirmware: Boolean
  ) extends SelfTestStatus

  case class Fatal(deviceSpecificCode: Byte) extends SelfTestStatus

  case object Reserved extends SelfTestStatus

  case class DeviceSpecificInternalFailure(deviceSpecificCode: Byte) extends SelfTestStatus

  object SelfTestStatus {
    implicit val decoder: Decoder[SelfTestStatus] = new Decoder[SelfTestStatus] {

      def decode(data: ByteString): SelfTestStatus = data(0).toUnsignedInt match {
        case 0x55 => NoError
        case 0x56 => NotImplemented
        case 0x57 => data(1).as[Corrupted]
        case 0x58 => Fatal(data(1))
        case 0xFF => Reserved
        case _    => DeviceSpecificInternalFailure(data(1))
      }
    }
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val selfTestStatus = is.read(2).as[SelfTestStatus]

        CommandResult(selfTestStatus)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(selfTestStatus: SelfTestStatus) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x04)
  }

}
