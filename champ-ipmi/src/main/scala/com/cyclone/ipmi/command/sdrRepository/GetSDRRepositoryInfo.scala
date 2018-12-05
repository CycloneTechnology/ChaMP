package com.cyclone.ipmi.command.sdrRepository

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.cyclone.ipmi.sdr.SdrVersion
import org.joda.time.Instant

/**
  * Get SDR Repository Info command and response
  */
object GetSDRRepositoryInfo {

  sealed trait RepositoryUpdateOperationSupport

  object RepositoryUpdateOperationSupport {
    implicit val decoder: Decoder[RepositoryUpdateOperationSupport] = new Decoder[RepositoryUpdateOperationSupport] {
      def decode(data: ByteString): RepositoryUpdateOperationSupport =
        data(0).toUnsignedInt match {
          case 0 => ModalNonModalSDRRepositoryUpdateOperationUnspecified
          case 1 => NonModalSDRRepositoryUpdateOperationSupported
          case 2 => ModalSDRRepositoryUpdateOperationSupported
          case 3 => BothModalAndNonModalSDRRepositoryUpdateOperationSupported
        }
    }

    case object ModalNonModalSDRRepositoryUpdateOperationUnspecified extends RepositoryUpdateOperationSupport

    case object NonModalSDRRepositoryUpdateOperationSupported extends RepositoryUpdateOperationSupport

    case object ModalSDRRepositoryUpdateOperationSupported extends RepositoryUpdateOperationSupport

    case object BothModalAndNonModalSDRRepositoryUpdateOperationSupported extends RepositoryUpdateOperationSupport

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val sdrVersion = is.readByte.as[SdrVersion]

        val recordCount = is.read(2).as[Short].toInt
        val freeSpace = is.read(2).as[Short].toInt

        val mostRecentAdditionTimestamp = is.read(4).as[Instant]
        val mostRecentEraseTimestamp = is.read(4).as[Instant]

        val operationSupport = is.readByte
        val overflowFlag = operationSupport.bit7
        val repositoryUpdateOperationSupport = operationSupport.bits5To6.as[RepositoryUpdateOperationSupport]

        CommandResult(
          sdrVersion,
          recordCount,
          freeSpace,
          mostRecentAdditionTimestamp,
          mostRecentEraseTimestamp,
          overflowFlag,
          repositoryUpdateOperationSupport)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    sdrVersion: SdrVersion,
    recordCount: Int,
    freeSpace: Int,
    mostRecentAdditionTimestamp: Instant,
    mostRecentEraseTimestamp: Instant,
    overflowFlag: Boolean,
    repositoryUpdateOperationSupport: RepositoryUpdateOperationSupport) extends IpmiCommandResult

  object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString =
        ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] = CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.StorageRequest
    val commandCode = CommandCode(0x20)
  }

}


