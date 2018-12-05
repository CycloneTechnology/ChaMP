package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

case class ParameterRevision(present: Int, required: Int)

object ParameterRevision {
  implicit val decoder: Decoder[ParameterRevision] = new Decoder[ParameterRevision] {
    def decode(data: ByteString) =
      ParameterRevision(
        present = data(0).bits4To7.toUnsignedInt,
        required = data(0).bits0To3.toUnsignedInt
      )
  }
}

/**
  * Get System Info Parameters command and response
  */
object GetSystemInfoParameters {

  case object ParameterNotSupported extends StatusCodeError {
    val code = StatusCode(0x80.toByte)
    val message = "Parameter not supported"
  }

  trait ParameterSelector {
    def code: Int
  }

  object ParameterSelector {
    implicit def coder[P <: ParameterSelector]: Coder[P] = new Coder[P] {
      def encode(a: P) = ByteString(a.code)
    }

    case object SetInProgress extends ParameterSelector {
      val code = 0x00

      sealed trait SetState

      object SetState {

        case object Complete extends SetState

        case object InProgress extends SetState

        case object CommitWrite extends SetState

        case object Reserved extends SetState

      }

      case class Data(setState: SetState)

      object Data {
        implicit val decoder: Decoder[Data] = new Decoder[Data] {
          def decode(data: ByteString): Data = {
            val setState = data(0).toUnsignedInt match {
              case 0 => SetState.Complete
              case 1 => SetState.InProgress
              case 2 => SetState.CommitWrite
              case 3 => SetState.Reserved
            }

            Data(setState)
          }
        }
      }

      implicit val parameterCodec: ParameterCodec[SetInProgress.type, Data, Nothing] = ParameterCodec.parameterCodecFor[SetInProgress.type, SetInProgress.Data]
    }

    case class BlockData(setSelector: Int, data: ByteString)

    object BlockData {
      implicit val decoder: Decoder[BlockData] = new Decoder[BlockData] {
        def decode(data: ByteString) =
          BlockData(data(0), data.drop(1))
      }
    }

    case class LongString(value: String)

    object LongString {
      implicit val blockDecoder: BlockDecoder[LongString] = new BlockDecoder[LongString] {
        def decode(encoding: StringDecoder, data: ByteString) =
          LongString(data.as(encoding))
      }
    }

    case class ShortString(value: String)

    object ShortString {
      implicit val decoder: Decoder[ShortString] = new Decoder[ShortString] {
        def decode(data: ByteString): ShortString = {
          val len = data(0).toUnsignedInt
          ShortString(data.drop(1).take(len).as(StringDecoder.AsciiLatin))
        }
      }
    }

    case object SystemFirmwareVersion extends ParameterSelector {
      val code = 0x01

      implicit val parameterCodec: ParameterCodec[SystemFirmwareVersion.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[SystemFirmwareVersion.type, BlockData, LongString]
    }

    case object SystemName extends ParameterSelector {
      val code = 0x02

      implicit val parameterCodec: ParameterCodec[SystemName.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[SystemName.type, BlockData, LongString]
    }

    case object PrimaryOperatingSystemName extends ParameterSelector {
      val code = 0x03

      implicit val parameterCodec: ParameterCodec[PrimaryOperatingSystemName.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[PrimaryOperatingSystemName.type, BlockData, LongString]
    }

    case object OperatingSystemName extends ParameterSelector {
      val code = 0x04

      implicit val parameterCodec: ParameterCodec[OperatingSystemName.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[OperatingSystemName.type, BlockData, LongString]
    }

    case object PresentOsVersionNumber extends ParameterSelector {
      val code = 0x05

      implicit val parameterCodec: ParameterCodec[PresentOsVersionNumber.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[PresentOsVersionNumber.type, BlockData, LongString]
    }

    case object BmcUrl extends ParameterSelector {
      val code = 0x06

      implicit val parameterCodec: ParameterCodec[BmcUrl.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[BmcUrl.type, BlockData, LongString]
    }

    case object BaseOsHypervisorUrlForManageability extends ParameterSelector {
      val code = 0x07

      implicit val parameterCodec: ParameterCodec[BaseOsHypervisorUrlForManageability.type, BlockData, LongString] =
        ParameterCodec.blockParameterCodecFor[BaseOsHypervisorUrlForManageability.type, BlockData, LongString]
    }

    // NB: OEM codes can be specified in OEM specific object modules
  }

  trait BlockDecoder[B] {
    def decode(encoding: StringDecoder, data: ByteString): B
  }

  object BlockDecoder {

    implicit object NoBlockDecoder extends BlockDecoder[Nothing] {
      def decode(encoding: StringDecoder, data: ByteString) = throw new UnsupportedOperationException
    }

  }

  /**
    * [[ParameterSelector]] specific response data.
    *
    * Associate selector and response types
    * by providing an implicit [[ParameterCodec]] instance inside the
    * [[ParameterSelector]]
    *
    * Some parameters will be encoded in the result of a single command.
    * Others require multiple commands to create a block of data that is decoded.
    */
  object ParameterCodec {
    def parameterCodecFor[P <: ParameterSelector : Coder, D: Decoder]: ParameterCodec[P, D, Nothing] =
      ParameterCodec[P, D, Nothing](implicitly[Coder[P]], implicitly[Decoder[D]], BlockDecoder.NoBlockDecoder)

    def blockParameterCodecFor[P <: ParameterSelector : Coder, D: Decoder, B: BlockDecoder] =
      ParameterCodec(implicitly[Coder[P]], implicitly[Decoder[D]], implicitly[BlockDecoder[B]])
  }

  case class ParameterCodec[P <: ParameterSelector, D, B](
    coder: Coder[P], decoder: Decoder[D], blockDecoder: BlockDecoder[B])

  object CommandResult {
    implicit def decoder[D: Decoder]: Decoder[CommandResult[D]] = new Decoder[CommandResult[D]] {
      def decode(data: ByteString): CommandResult[D] = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val parameterRevision = is.readByte.as[ParameterRevision]

        val responseData = iterator.toByteString.as[D]

        CommandResult(parameterRevision, responseData)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult[_]] = StatusCodeTranslator[CommandResult[_]] {
      case ParameterNotSupported.code => ParameterNotSupported
    }
  }

  case class CommandResult[+D: Decoder](
    parameterRevision: ParameterRevision,
    data: D) extends IpmiCommandResult

  object Command {
    implicit def coder[P <: ParameterSelector : Coder]: Coder[Command[P]] = new Coder[Command[P]] {
      def encode(request: Command[P]): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += 0x00.toByte // bit7 0b = Get Parameter, 1b = Get Parameter Revision Only, bits0to6 - reserved
        b ++= parameterSelector.toBin
        b += setSelector.toByte
        b += blockSelector.toByte

        b.result()
      }
    }

    implicit def codec[P <: ParameterSelector, D](implicit codec: ParameterCodec[P, D, _]): CommandResultCodec[Command[P], CommandResult[D]] = {
      implicit val decoder: Decoder[D] = codec.decoder
      implicit val coder: Coder[P] = codec.coder
      CommandResultCodec.commandResultCodecFor[Command[P], CommandResult[D]]
    }
  }

  case class Command[P <: ParameterSelector](
    parameterSelector: P,
    setSelector: Int = 0,
    blockSelector: Int = 0) extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x59)
  }

}
