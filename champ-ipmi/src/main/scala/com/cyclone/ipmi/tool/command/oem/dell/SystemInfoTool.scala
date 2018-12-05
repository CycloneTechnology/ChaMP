package com.cyclone.ipmi.tool.command.oem.dell

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters
import com.cyclone.ipmi.command.ipmiMessagingSupport.GetSystemInfoParameters.ParameterSelector.{LongString, ShortString}
import com.cyclone.ipmi.command.oem.dell.GetSystemInfoParameterSelectors.IdracInfo.IdracType
import com.cyclone.ipmi.command.oem.dell.GetSystemInfoParameterSelectors._
import com.cyclone.ipmi.command.oem.dell.GetSystemInfoParametersEx
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.SystemInfoParameterExtractor._
import com.cyclone.ipmi.tool.command.{IpmiToolCommand, IpmiToolCommandResult}
import com.cyclone.ipmi.{IpmiError, IpmiOperationContext}
import com.cyclone.util.concurrent.Futures
import com.typesafe.scalalogging.LazyLogging
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetSystemInfoParameters]] low-level command to specifically obtain the Dell Asset Tag.
  */
object SystemInfoTool {

  object Command extends IpmiToolCommand with LazyLogging {
    implicit val executor: CommandExecutor[Command.type, Result] = new CommandExecutor[Command.type, Result] {
      def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {

        val result = for {
          guid <- eitherT(extractSimple(Guid))
          assetTag <- eitherT(extractSimple(AssetTag))
          serviceTag <- eitherT(extractSimple(ServiceTag))
          chassisServiceTag <- eitherT(extractSimple(ChassisServiceTag))
          chassisRelatedServiceTag <- eitherT(extractSimple(ChassisRelatedServiceTag))
          boardRevision <- eitherT(extractSimple(BoardRevision))
          platformModelName <- eitherT(extractBlock(PlatformModelName))
          bladeSlotInfo <- eitherT(extractBlock(BladeSlotInfo))
          iDracIpV4Url <- eitherT(extractBlock(iDracIpV4Url))
          cmcIpV4Url <- eitherT(extractBlock(CmcIpV4Url))
          cmcIpV6Url <- eitherT(extractBlock(CmcIpV6Url))
          iDracInfo <- eitherT(extractBlock(IdracInfo))

          macAddresses <- iDracInfo match {
            case Some(info) if info.`type` == IdracType.`11G Modular`    => eitherT(macAddressFor11Or12G)
            case Some(info) if info.`type` == IdracType.`11G Monolithic` => eitherT(macAddressFor11Or12G)
            case Some(info) if info.`type` == IdracType.`12G Modular`    => eitherT(macAddressFor11Or12G)
            case Some(info) if info.`type` == IdracType.`12G Monolithic` => eitherT(macAddressFor11Or12G)
            case _                                                       => eitherT(extractSimple(MacAddressesFor10G))
          }
        } yield Result(
          guid = guid,
          assetTag = assetTag,
          serviceTag = serviceTag,
          chassisServiceTag = chassisServiceTag,
          chassisRelatedServiceTag = chassisRelatedServiceTag,
          boardRevision = boardRevision,
          platformModelName = platformModelName,
          bladeSlotInfo = bladeSlotInfo,
          iDracIpV4Url = iDracIpV4Url,
          cmcIpV4Url = cmcIpV4Url,
          icmcIpV6Url = cmcIpV6Url,
          iDracInfo = iDracInfo,
          macAddresses = macAddresses,
          macAddressList = macAddresses.map(_.macAddresses.mkString(","))
        )

        result.run
      }

      private def macAddressFor11Or12G(implicit ctx: IpmiOperationContext): Future[IpmiErrorOr[Option[MacAddresses]]] = {
        implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
        import ctx._

        logger.debug(s"Getting 11/12G mac addresses")

        val macBlockLength = 8

        def getSize: Future[IpmiErrorOr[Int]] = {
          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetSystemInfoParametersEx.Command(MacAddressesFor11Or12G)))
          } yield {
            logger.debug(s"Getting 11/12G mac address size from $cmdResult")
            cmdResult.responseData(0).toUnsignedInt
          }

          result.run
        }

        def getMacAddress(readOffset: Int): Future[IpmiErrorOr[String]] = {
          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetSystemInfoParametersEx.Command(
              parameterSelector = MacAddressesFor11Or12G,
              readOffset = readOffset,
              readLength = macBlockLength
            )))
          } yield {
            logger.debug(s"Getting 11/12G mac address for offset $readOffset from ${cmdResult.responseData.toHexString()}")
            cmdResult.responseData.drop(2).toHexString()
          }

          result.run
        }

        val result = for {
          size <- eitherT(getSize)

          indices = 0 until size / macBlockLength

          macAddresses <- eitherT(Futures.traverseSerially(indices) { index =>
            getMacAddress(index * macBlockLength)
          })
        } yield MacAddresses(macAddresses)

        recoverNotSupportedToOption(result.run)
      }
    }

    def description() = "dell system-info"
  }

  case class Result(
    guid: Option[Guid.Data],
    assetTag: Option[ShortString],
    serviceTag: Option[ShortString],
    chassisServiceTag: Option[ShortString],
    chassisRelatedServiceTag: Option[ShortString],
    boardRevision: Option[ShortString],
    platformModelName: Option[LongString],
    bladeSlotInfo: Option[LongString],
    iDracIpV4Url: Option[LongString],
    cmcIpV4Url: Option[LongString],
    icmcIpV6Url: Option[LongString],
    iDracInfo: Option[IdracInfo.Data],
    macAddresses: Option[MacAddresses],
    macAddressList: Option[String]
  ) extends IpmiToolCommandResult

}
