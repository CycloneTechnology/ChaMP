package com.cyclone.ipmi.protocol.packet

import akka.util.{ByteString, ByteStringBuilder}
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.global.DeviceAddress
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCode}

object StandardCommandWrapper {

  object RequestPayload {
    implicit def coder: Coder[RequestPayload] = new Coder[RequestPayload] {

      def encode(payload: RequestPayload): ByteString = {
        import payload._

        val b = new ByteStringBuilder

        b ++= targetAddress.toBin
        b += (networkFunction.code << 2 | responderLun).toByte
        b += checksum(b.result().take(2))
        b ++= localAddress.toBin
        b += (seqNo.toByte | requesterLun).toByte

        b ++= commandCode.toBin

        b ++= commandData
        b += checksum(b.result().drop(3))

        b.result()
      }
    }

    def fromCommand[Cmd <: IpmiStandardCommand: Coder](
      command: Cmd,
      seqNo: SeqNo,
      targetAddress: DeviceAddress = DeviceAddress.BmcAddress
    ): RequestPayload = {
      RequestPayload(
        command.networkFunction,
        command.commandCode,
        seqNo,
        targetAddress,
        implicitly[Coder[Cmd]].encode(command)
      )
    }
  }

  case class RequestPayload(
    networkFunction: NetworkFunction,
    commandCode: CommandCode,
    seqNo: SeqNo,
    targetAddress: DeviceAddress,
    commandData: ByteString
  ) extends IpmiRequestPayload {
    val requesterLun: Byte = 0.toByte
    val responderLun: Byte = 0.toByte
    val localAddress: DeviceAddress = DeviceAddress.RemoteConsoleAddress
    val payloadType: PayloadType = PayloadType.Ipmi
  }

  object ResponsePayload {
    implicit def decoder: Decoder[ResponsePayload] = new Decoder[ResponsePayload] {

      def decode(data: ByteString): ResponsePayload = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val reqAdd = is.readByte.as[DeviceAddress]

        val netReqLun = is.readByte
        val networkFunction = netReqLun.bits2To7.as[NetworkFunction]
        val reqLun = netReqLun.bits0To1

        require(is.readByte == checksum(data.take(2)))

        val respAdd = is.readByte.as[DeviceAddress]

        val seqRespLun = is.readByte
        val seqNumber = seqRespLun.as[SeqNo]
        val respLun = seqRespLun.bits0To1

        val cmdCode = is.readByte.as[CommandCode]
        val statusCode = is.readByte.as[StatusCode]

        val remaining = iterator.toByteString
        val (resp, check) = remaining.splitAt(remaining.length - 1)

        require(check(0) == checksum(data.drop(3).take(data.length - 4)))

        ResponsePayload(
          resultData = resp,
          statusCode = statusCode,
          networkFunction = networkFunction,
          seqNo = seqNumber,
          commandCode = cmdCode,
          requesterLun = reqLun,
          responderLun = respLun,
          requesterAddress = reqAdd,
          responderAddress = respAdd
        )
      }
    }
  }

  case class ResponsePayload(
    resultData: ByteString,
    statusCode: StatusCode,
    networkFunction: NetworkFunction,
    seqNo: SeqNo,
    commandCode: CommandCode,
    requesterLun: Byte = 0.toByte,
    responderLun: Byte = 0.toByte,
    requesterAddress: DeviceAddress,
    responderAddress: DeviceAddress
  ) extends IpmiResponsePayload

}
