package com.cyclone.ipmi.command.oem.fujitsu.s4

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetEepromVersionInfo command and response
  */
object GetEepromVersionInfo {

  sealed trait EepromNumber

  object EepromNumber {
    implicit val decoder: Decoder[EepromNumber] = new Decoder[EepromNumber] {

      def decode(data: ByteString): EepromNumber = data(0).toUnsignedInt match {
        case 0x00 => Eeprom0
        case 0x01 => Eeprom1
      }
    }

    implicit val encoder: Coder[EepromNumber] = new Coder[EepromNumber] {

      def encode(a: EepromNumber): ByteString = {
        a match {
          case Eeprom0 => ByteString(0x00)
          case Eeprom1 => ByteString(0x01)
        }
      }
    }

    case object Eeprom0 extends EepromNumber

    case object Eeprom1 extends EepromNumber

  }

  sealed trait Status

  object Status {
    implicit val decoder: Decoder[Status] = new Decoder[Status] {

      def decode(data: ByteString): Status = data(0).toUnsignedInt match {
        case 0x00 => ChecksumErrorRuntimeFw
        case 0x01 => Ok
      }
    }

    implicit val encoder: Coder[Status] = new Coder[Status] {

      def encode(a: Status): ByteString = {
        a match {
          case ChecksumErrorRuntimeFw => ByteString(0x00)
          case Ok                     => ByteString(0x01)
        }
      }
    }

    case object ChecksumErrorRuntimeFw extends Status

    case object Ok extends Status

  }

  object CommandResult extends IpmiCommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
//        val iterator = data.iterator
//        val is = iterator.asInputStream
//
//        val requestType = is.read(3).as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)
//
//        val status = is.readByte.as[Status]
//
//        // FIXME - Check that we are doing the right thing with the following fields
//        val majorFwRevision = is.readByte // Binary coded
//        val minorFwRevision = is.readByte // BCD coded
//        val majorAuxFwRevision = is.readByte // Binary coded
//        val minorAuxFwRevision = is.readByte // Binary coded
//        val resAuxFwRevision = is.readByte // Binary coded
//        val majorFwRevisionAscii = is.readByte // ASCII coded letter
//        val majorSdrrRevision = is.readByte
//        val minorSdrrRevision = is.readByte
//        val sdrrRevisionChar = is.readByte
//        val sdrrIdLSB = is.readByte
//        val sdrrIdMSB = is.readByte
//        val majorBooterRevisionBinaryCoded = is.readByte
//        val majorBooterRevisionBcdCoded = is.readByte
//        val AuxBooterRevisionMajor = is.readByte // BinaryCoded
//        val AuxBooterRevisionMinor = is.readByte // BinaryCoded

        // FIXME - Return the appropriate fields from above
        CommandResult()
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult() extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x12.toByte // Command Specifier
        b ++= eepromNumber.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(eepromNumber: EepromNumber) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0xf5.toByte)
  }

}
