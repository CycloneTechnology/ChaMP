package com.cyclone.ipmi.tool.command

import akka.util.ByteString
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError._
import com.cyclone.ipmi.api.IpmiConnection
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.ParameterSelector.BlockData
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.{ParameterCodec, ParameterSelector}
import com.cyclone.ipmi.IpmiOperationContext
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Utilities to extract parameters using
  * [[GetSystemInfoParameters]] commands.
  */
object SystemInfoParameterExtractor extends LazyLogging {

  /**
    * Extracts a parameter from a single command execution.
    */
  def extractSimple[P <: ParameterSelector, D](selector: P)(
    implicit ctx: IpmiOperationContext,
    parameterCodec: ParameterCodec[P, D, Nothing]
  ): Future[IpmiErrorOr[Option[D]]] = {
    implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
    import ctx._

    val result = for {
      cmdResult <- eitherT(
        connection.executeCommandOrError(GetSystemInfoParameters.Command(selector))
      )

      _ = logger.debug(s"Result=$cmdResult")
    } yield
      cmdResult match {
        case GetSystemInfoParameters.CommandResult(_, d) => d
      }

    logger.debug(s"Extracting simple parameter data for $selector")
    recoverNotSupportedToOption(result.run)
  }

  /**
    * Extracts a block of data from multiple executions of the command.
    *
    * Most parameter info data blocks are of the following form (as described in the IPMI spec):
    *
    * 1st byte = set selector
    * 2nd byte
    * - 7:4 - reserved
    * - 3:0 - string encoding, 0 = printable ascii
    * 3rd byte = string length
    * ? bytes = string
    *
    * Set Selector > 0
    *
    * 1st byte = set selector
    * ? bytes = string
    *
    * etc...
    *
    * Some use cases have this form even though they are not treating the data as as string.
    * So this utility just pulls out and concatenates the data up to the required string length
    * and returns it along with the encoding that can be used if desired.
    */
  def extractBlock[P <: ParameterSelector, B](selector: P)(
    implicit ctx: IpmiOperationContext,
    parameterCodec: ParameterCodec[P, BlockData, B]
  ): Future[IpmiErrorOr[Option[B]]] = {
    implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
    import ctx._

    def loop(
      setSelector: Int,
      lengthAndCharset: Option[(Int, StringDecoder)],
      acc: ByteString
    ): Future[IpmiErrorOr[(StringDecoder, ByteString)]] = {
      lengthAndCharset match {
        case Some((len, enc)) if acc.length >= len =>
          // We're done...
          (enc, acc).right.point[Future]

        case _ =>
          val result = for {
            cmdResult <- eitherT(
              connection.executeCommandOrError(
                GetSystemInfoParameters.Command(selector, setSelector = setSelector)
              )
            )

            _ = logger.debug(s"Selector $setSelector result=$cmdResult")

            (length, charset, newAcc) = cmdResult match {
              case GetSystemInfoParameters.CommandResult(_, BlockData(_, data)) =>
                lengthAndCharset match {
                  case None =>
                    val decoder = data(0).toUnsignedInt match {
                      case 0 => StringDecoder.AsciiLatin
                      case 1 => StringDecoder.Utf8
                      case 2 => StringDecoder.Unicode
                    }

                    (data(1).toUnsignedInt, decoder, data.drop(2))

                  case Some((l, c)) => (l, c, acc ++ data)
                }
            }

            charsetAndData <- eitherT(loop(setSelector + 1, Some(length, charset), newAcc))
          } yield charsetAndData

          result.run
      }
    }

    logger.debug(s"Extracting block parameter data for $selector")
    val result = for {
      charsetAndData <- eitherT(loop(0, None, ByteString.empty))
    } yield
      charsetAndData match {
        case (dec, data) => parameterCodec.blockDecoder.decode(dec, data)
      }

    recoverNotSupportedToOption(result.run)
  }

  /**
    * Recovers from an unsupported parameter by returning an empty (None) value
    */
  def recoverNotSupportedToOption[D](
    futureErrorOrValue: Future[IpmiErrorOr[D]]
  ): Future[IpmiErrorOr[Option[D]]] =
    futureErrorOrValue.map { errorOrValue =>
      errorOrValue
        .map(Some(_))
        .recover {
          case GetSystemInfoParameters.ParameterNotSupported =>
            logger.debug("Ignoring ParameterNotSupported")
            None
        }
    }
}
