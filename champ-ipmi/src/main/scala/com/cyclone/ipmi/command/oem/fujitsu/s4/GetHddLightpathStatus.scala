import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.StatusCodeError
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.command.{CommandCode, StatusCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}

/**
  * GetHddLightpathStatus command and response
  *
  * FIXME - This needs completing and the corresponding class for the s2/s3 fujitsu devices need creating (more than likely a straight copy of this)
  */
object GetHddLightpathStatus {

  case object SignalNotAvailable extends StatusCodeError {
    val code = StatusCode(0x01.toByte)

    val message =
      "Status Signal not available"
  }

  case object ComponentNotPresent extends StatusCodeError {
    val code = StatusCode(0x02.toByte)

    val message =
      "Component not present"
  }

  sealed trait SignalStatus

  object SignalStatus {
    implicit val decoder: Decoder[SignalStatus] = new Decoder[SignalStatus] {

      def decode(data: ByteString): SignalStatus = data(0).toUnsignedInt match {
        case 0x00 => Ok
        case 0x01 => Identify
        case 0x02 => PrefailureWarning
        case 0x03 => Failure
      }
    }

    implicit val encoder: Coder[SignalStatus] = new Coder[SignalStatus] {

      def encode(a: SignalStatus): ByteString = {
        a match {
          case Ok                => ByteString(0x00)
          case Identify          => ByteString(0x01)
          case PrefailureWarning => ByteString(0x02)
          case Failure           => ByteString(0x03)
        }
      }
    }

    case object Ok extends SignalStatus

    case object Identify extends SignalStatus

    case object PrefailureWarning extends SignalStatus

    case object Failure extends SignalStatus

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        //        val iterator = data.iterator
        //        val is = iterator.asInputStream
        //
        //        val requestType = is.read(3).as[IanaEnterpriseNumber] // Should match the IANA value present in the request (0x80 0x28 0x00 in this case)
        //
        //        val signalStatus = is.readByte.as[SignalStatus]

        // FIXME - Complete this - See Fujitsu spec

        CommandResult()
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult] {
        case SignalNotAvailable.code  => SignalNotAvailable
        case ComponentNotPresent.code => ComponentNotPresent
      }
  }

  case class CommandResult() extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {

      def encode(request: Command.type): ByteString = {

        val b = new ByteStringBuilder

        val fujitsu: IanaEnterpriseNumber = IanaEnterpriseNumber.Fujitsu
        b ++= fujitsu.toBin

        b += 0x42.toByte // Command Specifier

        // FIXME - TBD - See Fujitsu Spec

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.FujitsuGroupRequest
    val commandCode = CommandCode(0xf5.toByte)
  }

}
