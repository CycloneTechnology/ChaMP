package com.cyclone.ipmi.tool.command

import akka.util.ByteString
import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.command.global.GetDeviceId.DeviceAvailable
import com.cyclone.ipmi.command.global._
import com.cyclone.ipmi.command.oem.IanaEnterpriseNumber
import com.cyclone.ipmi.protocol.sdr.DeviceCapabilities
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.IpmiError
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the [[GetDeviceId]] low-level command.
  */
object DeviceIdTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetDeviceId.Command))
          } yield {
            import cmdResult._

            Result(
              deviceId = deviceId,
              deviceSdrsProvided = deviceSdrsProvided,
              deviceRevision = deviceRevision,
              deviceAvailable = deviceAvailable,
              firmwareRevision = firmwareRevision,
              ipmiVersion = ipmiVersion,
              deviceCapabilities = deviceCapabilities,
              manufacturerId = manufacturerId,
              productId = productId,
              auxiliaryFirmwareRevisionInformation = auxiliaryFirmwareRevisionInformation
            )
          }

          result.run
        }
      }

    def description() = "bmc info"
  }

  case class Result(
    deviceId: DeviceId,
    deviceSdrsProvided: Boolean,
    deviceRevision: DeviceRevision,
    deviceAvailable: DeviceAvailable,
    firmwareRevision: FirmwareRevision,
    ipmiVersion: IpmiVersion,
    deviceCapabilities: DeviceCapabilities,
    manufacturerId: IanaEnterpriseNumber,
    productId: ProductId,
    auxiliaryFirmwareRevisionInformation: Option[ByteString]
  ) extends IpmiToolCommandResult

}
