package com.cyclone.ipmi.command.ipmiMessagingSupport

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet._

/**
  * Get Channel Cipher Suites command and response
  */
object GetChannelCipherSuites {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val chan = is.readByte
        val csData = iterator.toByteString

        CommandResult(
          channelNumber = chan,
          cipherSuitesData = csData
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  /**
    * Response for [[GetChannelCipherSuites.Command]].
    *
    * Note (spec sec 22.15) cipher suites are returned in data of multiple responses for
    * different listIndex values (0, 1, ...).
    *
    * The encoded records may be split between multiple records.
    */
  case class CommandResult(channelNumber: Byte, cipherSuitesData: ByteString) extends IpmiCommandResult {

    /**
      * @return whether this holds the last block of cipher suite data
      */
    def last: Boolean = cipherSuitesData.length < 16
  }

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {

      def encode(request: Command): ByteString = {
        import request._

        val b = new ByteStringBuilder

        b += channelNumber
        b ++= payloadType.toBin

        val idx = (0x80 | (listIndex & 0x3F)).toByte
        b += idx

        b.result()
      }
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command(listIndex: Int) extends IpmiStandardCommand {
    val networkFunction: NetworkFunction = NetworkFunction.ApplicationRequest
    val commandCode = CommandCode(0x54)

    // The Payload Type number is used to look up the Security Algorithm support
    // when establishing a separate session for a given payload type.
    // (NOT tied to the payload type of the request!)
    val payloadType: PayloadType = PayloadType.Ipmi
  }

}
