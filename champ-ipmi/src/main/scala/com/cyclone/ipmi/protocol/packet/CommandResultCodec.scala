package com.cyclone.ipmi.protocol.packet

import com.cyclone.ipmi.codec.{Coder, Decoder}
import com.cyclone.ipmi.command.StatusCodeTranslator

object CommandResultCodec {
  def commandResultCodecFor[Cmd <: IpmiCommand : Coder, Res <: IpmiCommandResult : Decoder]
  (implicit statusCodeTranslator: StatusCodeTranslator[Res]): CommandResultCodec[Cmd, Res] =
    CommandResultCodec(
      implicitly[Coder[Cmd]],
      implicitly[Decoder[Res]],
      statusCodeTranslator)
}

/**
  * Codec for command and corresponding result
  */
case class CommandResultCodec[Cmd <: IpmiCommand, Res <: IpmiCommandResult](
  coder: Coder[Cmd], decoder: Decoder[Res],
  statusCodeTranslator: StatusCodeTranslator[Res])


