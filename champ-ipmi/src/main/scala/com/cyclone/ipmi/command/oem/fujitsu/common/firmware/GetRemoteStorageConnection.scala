package com.cyclone.ipmi.command.oem.fujitsu.common.firmware

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * One format for the GetRemoteStorageConnectionOrStatus command and response
  */
object GetRemoteStorageConnection {

  sealed trait Connection

  object Connection {
    implicit val decoder: Decoder[Connection] = new Decoder[Connection] {
      def decode(data: ByteString): Connection = data(0).toUnsignedInt match {
        case 0x00 => Connection0
        case 0x01 => Connection1
      }
    }

    implicit val encoder: Coder[Connection] = new Coder[Connection] {
      def encode(a: Connection): ByteString = {
        a match {
          case Connection0 => ByteString(0x00)
          case Connection1 => ByteString(0x01)
        }
      }
    }

    case object Connection0 extends Connection

    case object Connection1 extends Connection

  }

  sealed trait ConnectionState

  object ConnectionState {
    implicit val decoder: Decoder[ConnectionState] = new Decoder[ConnectionState] {
      def decode(data: ByteString): ConnectionState = data(0).toUnsignedInt match {
        case 0x00 => InvalidOrUnknown
        case 0x01 => Idle
        case 0x02 => ConnectionAttemptPending
        case 0x03 => Connected
        case 0x04 => ConnectionAttemptsRetriesExhaustedOrFailed
        case 0x05 => ConnectionLost
        case 0x06 => DisconnectPending
      }
    }

    implicit val encoder: Coder[ConnectionState] = new Coder[ConnectionState] {
      def encode(a: ConnectionState): ByteString = {
        a match {
          case InvalidOrUnknown                           => ByteString(0x00)
          case Idle                                       => ByteString(0x01)
          case ConnectionAttemptPending                   => ByteString(0x02)
          case Connected                                  => ByteString(0x03)
          case ConnectionAttemptsRetriesExhaustedOrFailed => ByteString(0x04)
          case ConnectionLost                             => ByteString(0x05)
          case DisconnectPending                          => ByteString(0x06)
        }
      }
    }

    case object InvalidOrUnknown extends ConnectionState

    case object Idle extends ConnectionState

    case object ConnectionAttemptPending extends ConnectionState

    case object Connected extends ConnectionState

    case object ConnectionAttemptsRetriesExhaustedOrFailed extends ConnectionState

    case object ConnectionLost extends ConnectionState

    case object DisconnectPending extends ConnectionState

  }

  sealed trait StorageType

  object StorageType {
    implicit val decoder: Decoder[StorageType] = new Decoder[StorageType] {
      def decode(data: ByteString): StorageType = data(0).toUnsignedInt match {
        case 0x00 => InvalidOrUnknown
        case 0x01 => StorageServerOrIpmi
        case 0x02 => Applet
        case 0x03 => NoneOrNotConnected
      }
    }

    implicit val encoder: Coder[StorageType] = new Coder[StorageType] {
      def encode(a: StorageType): ByteString = {
        a match {
          case InvalidOrUnknown    => ByteString(0x00)
          case StorageServerOrIpmi => ByteString(0x01)
          case Applet              => ByteString(0x02)
          case NoneOrNotConnected  => ByteString(0x03)
        }
      }
    }

    case object InvalidOrUnknown extends StorageType

    case object StorageServerOrIpmi extends StorageType

    case object Applet extends StorageType

    case object NoneOrNotConnected extends StorageType

  }

  sealed trait Connected

  object Connected {
    implicit val decoder: Decoder[Connected] = new Decoder[Connected] {
      def decode(data: ByteString): Connected = data(0).toUnsignedInt match {
        case 0x00 => No
        case 0x01 => Yes
      }
    }

    implicit val encoder: Coder[Connected] = new Coder[Connected] {
      def encode(a: Connected): ByteString = {
        a match {
          case No  => ByteString(0x00)
          case Yes => ByteString(0x01)
        }
      }
    }

    case object No extends Connected

    case object Yes extends Connected

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {
      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        /*val requestType = */is.readByte // Should match the value in the request (0x02 in this case)

        /*val unused = */is.read(2)

        val connectionState = is.readByte.as[ConnectionState]
        val storageType = is.readByte.as[StorageType]

        CommandResult(connectionState, storageType)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] = StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(connectionState: ConnectionState, storageType: StorageType) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += 0x02.toByte // requestType

        b += 0x00.toByte

        b ++= connection.toBin

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] = CommandResultCodec.commandResultCodecFor[Command, CommandResult]

  }

  case class Command(connection: Connection) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuOemRequest
    val commandCode = CommandCode(0x19)
  }

}
