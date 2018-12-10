package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetNicSelectionFailover
  */
object GetNicSelectionFailover {

  sealed trait Selection

  object Selection {

    implicit val decoder: Decoder[Selection] = new Decoder[Selection] {

      def decode(data: ByteString): Selection = data(0).toUnsignedInt match {
        case 0x01 => Dedicated
        case 0x02 => Lom1
        case 0x03 => Lom2
        case 0x04 => Lom3
        case 0x05 => Lom4
      }
    }

    implicit val encoder: Coder[Selection] = new Coder[Selection] {

      def encode(a: Selection): ByteString = {
        a match {
          case Dedicated => ByteString(0x01)
          case Lom1      => ByteString(0x02)
          case Lom2      => ByteString(0x03)
          case Lom3      => ByteString(0x04)
          case Lom4      => ByteString(0x05)
        }
      }
    }

    case object Dedicated extends Selection

    case object Lom1 extends Selection

    case object Lom2 extends Selection

    case object Lom3 extends Selection

    case object Lom4 extends Selection

  }

  sealed trait Failover

  object Failover {

    implicit val decoder: Decoder[Failover] = new Decoder[Failover] {

      def decode(data: ByteString): Failover = data(0).toUnsignedInt match {
        case 0x00 => None
        case 0x01 => Lom1
        case 0x02 => Lom2
        case 0x03 => Lom3
        case 0x04 => Lom4
        case 0x05 => All
      }
    }

    implicit val encoder: Coder[Failover] = new Coder[Failover] {

      def encode(a: Failover): ByteString = {
        a match {
          case None => ByteString(0x00)
          case Lom1 => ByteString(0x01)
          case Lom2 => ByteString(0x02)
          case Lom3 => ByteString(0x03)
          case Lom4 => ByteString(0x04)
          case All  => ByteString(0x05)
        }
      }
    }

    case object None extends Failover

    case object Lom1 extends Failover

    case object Lom2 extends Failover

    case object Lom3 extends Failover

    case object Lom4 extends Failover

    case object All extends Failover

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nicSelection = is.read(1).as[Selection]
        val nicFailover = is.read(1).as[Failover]

        CommandResult(nicSelection, nicFailover)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nicSelection: Selection, nicFailover: Failover) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.Oem29Request
    val commandCode = CommandCode(0x25)
  }

}
