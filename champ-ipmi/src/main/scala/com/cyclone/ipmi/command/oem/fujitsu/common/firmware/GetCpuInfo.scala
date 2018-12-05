package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetCpuInfo command and response
  */
object GetCpuInfo {

  case object UnpopulatedSocket extends StatusCodeError {
    val code = StatusCode(0x01.toByte)

    val message =
      "Unpopulated CPU Socket"
  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */
        is.read(3)
          .as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)

        val cpuId = is.read(2).as[Short]
        val platformId = is.readByte
        val brandId = is.readByte
        val maximalCoreSpeedOfCpuMHz = is.read(2).as[Short]
        val intelQuickpathInterconnectMegaTransactionsPerSecond = is.read(2).as[Short]
        val tControlOffset = is.readByte
        val tDiodeOffset = is.readByte
        val cpuDataSpare = is.readByte
        val RecordIdCpuInfoSdr = is.read(2).as[Short]
        val RecordIdFanControlSdr = is.read(2).as[Short]
        val CpuIdHighWord = is.read(2).as[Short]

        CommandResult(
          cpuId,
          platformId,
          brandId,
          maximalCoreSpeedOfCpuMHz,
          intelQuickpathInterconnectMegaTransactionsPerSecond,
          tControlOffset,
          tDiodeOffset,
          cpuDataSpare,
          RecordIdCpuInfoSdr,
          RecordIdFanControlSdr,
          CpuIdHighWord
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case UnpopulatedSocket.code => UnpopulatedSocket
      }
  }

  case class CommandResult(
    cpuId: Short,
    platformId: Byte,
    brandId: Byte,
    maximalCoreSpeedOfCpuMHz: Short,
    intelQuickpathInterconnectMegaTransactionsPerSecond: Short,
    tControlOffset: Byte,
    tDiodeOffset: Byte,
    cpuDataSpare: Byte,
    RecordIdCpuInfoSdr: Short,
    RecordIdFanControlSdr: Short,
    CpuIdHighWord: Short
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x15.toByte // Command Specifier
        b += socketNumber.toByte

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(socketNumber: Int) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0xf1.toByte)
  }

}
