package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.protocol.readingoffset._

sealed trait SensorType {
  type EROffset <: EventReadingOffset

  def code: Int

  def eventOffsetFor(bit: Int): Option[EROffset]
}

object SensorType {

  implicit val decoder: Decoder[SensorType] = new Decoder[SensorType] {

    def decode(data: ByteString): SensorType =
      data(0).toUnsignedInt match {
        case Temperature.code                  => Temperature
        case Voltage.code                      => Voltage
        case Current.code                      => Current
        case Fan.code                          => Fan
        case PhysicalSecurity.code             => PhysicalSecurity
        case PlatformSecurity.code             => PlatformSecurity
        case Processor.code                    => Processor
        case PowerSupply.code                  => PowerSupply
        case PowerUnit.code                    => PowerUnit
        case CoolingDevice.code                => CoolingDevice
        case Other.code                        => Other
        case Memory.code                       => Memory
        case DriveSlotBay.code                 => DriveSlotBay
        case PostMemoryResize.code             => PostMemoryResize
        case SystemFirmwareProgress.code       => SystemFirmwareProgress
        case EventLoggingDisabled.code         => EventLoggingDisabled
        case Watchdog1.code                    => Watchdog1
        case SystemEvent.code                  => SystemEvent
        case CriticalInterrupt.code            => CriticalInterrupt
        case ButtonSwitch.code                 => ButtonSwitch
        case ModuleBoard.code                  => ModuleBoard
        case MicrocontrollerCoprocessor.code   => MicrocontrollerCoprocessor
        case AddInCard.code                    => AddInCard
        case Chassis.code                      => Chassis
        case ChipSet.code                      => ChipSet
        case OtherFru.code                     => OtherFru
        case CableInterconnect.code            => CableInterconnect
        case Terminator.code                   => Terminator
        case SystemBootRestartInitiated.code   => SystemBootRestartInitiated
        case BootError.code                    => BootError
        case BaseOsBootInstallationStatus.code => BaseOsBootInstallationStatus
        case OsStopShutdown.code               => OsStopShutdown
        case SlotConnector.code                => SlotConnector
        case SystemAcpiPowerState.code         => SystemAcpiPowerState
        case Watchdog2.code                    => Watchdog2
        case PlatformAlert.code                => PlatformAlert
        case EntityPresence.code               => EntityPresence
        case MonitorAsicIc.code                => MonitorAsicIc
        case Lan.code                          => Lan
        case ManagementSubsystemHealth.code    => ManagementSubsystemHealth
        case Battery.code                      => Battery
        case SessionAudit.code                 => SessionAudit
        case VersionChange.code                => VersionChange
        case FruState.code                     => FruState
        case b if b.in(0xc0 to 0xff)           => Oem(b)
      }
  }

  /**
    * Maps type names used in ipmitool as well as here (for the sdr type <typename>) command.
    *
    * The names (although not the abbreviations) can be obtained from ipmitool by running the "sdr type" command
    * without specifying a type name.
    */

  def forName(typeName: String): Option[SensorType] = {

    val matcher: PartialFunction[String, SensorType] = {
      case "temp" | "temperature"             => Temperature
      case "volt" | "voltage"                 => Voltage
      case "current"                          => Current
      case "fan"                              => Fan
      case "physical security"                => PhysicalSecurity
      case "platform security"                => PlatformSecurity
      case "processor"                        => Processor
      case "power supply"                     => PowerSupply
      case "power unit"                       => PowerUnit
      case "cooling device"                   => CoolingDevice
      case "other units-based sensor"         => Other
      case "memory"                           => Memory
      case "drive slot (bay)"                 => DriveSlotBay
      case "POST memory resize"               => PostMemoryResize
      case "system firmware progress"         => SystemFirmwareProgress
      case "event logging disabled"           => EventLoggingDisabled
      case "watchdog 1"                       => Watchdog1
      case "system event"                     => SystemEvent
      case "critical interrupt"               => CriticalInterrupt
      case "button/switch"                    => ButtonSwitch
      case "module/board"                     => ModuleBoard
      case "microcontroller/coprocessor"      => MicrocontrollerCoprocessor
      case "add-in card"                      => AddInCard
      case "chassis"                          => Chassis
      case "chip set"                         => ChipSet
      case "other fru"                        => OtherFru
      case "cable/interconnect"               => CableInterconnect
      case "terminator"                       => Terminator
      case "system boot/restart initiated"    => SystemBootRestartInitiated
      case "boot error"                       => BootError
      case "base os boot/installation status" => BaseOsBootInstallationStatus
      case "os stop/shutdown"                 => OsStopShutdown
      case "slot/connector"                   => SlotConnector
      case "system acpi power state"          => SystemAcpiPowerState
      case "watchdog 2"                       => Watchdog2
      case "platform alert"                   => PlatformAlert
      case "entity presence"                  => EntityPresence
      case "monitor asic/ic"                  => MonitorAsicIc
      case "lan"                              => Lan
      case "management system health"         => ManagementSubsystemHealth
      case "battery"                          => Battery
      case "session audit"                    => SessionAudit
      case "version change"                   => VersionChange
      case "fru state"                        => FruState
    }

    matcher.lift(typeName.trim.toLowerCase)
  }

  case object Temperature extends SensorType {
    type EROffset = Nothing

    val code = 0x01

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Voltage extends SensorType {
    type EROffset = Nothing

    val code = 0x02

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Current extends SensorType {
    type EROffset = Nothing

    val code = 0x03

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Fan extends SensorType {
    type EROffset = Nothing

    val code = 0x04

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object PhysicalSecurity extends SensorType {
    type EROffset = PhysicalSecurityEventOffset

    val code = 0x05

    def eventOffsetFor(bit: Int): Option[EROffset] = PhysicalSecurityEventOffset.offsetFor(bit)

  }

  case object PlatformSecurity extends SensorType {
    type EROffset = PlatformSecurityEventOffset

    val code = 0x06

    def eventOffsetFor(bit: Int): Option[EROffset] = PlatformSecurityEventOffset.offsetFor(bit)
  }

  case object Processor extends SensorType {
    type EROffset = ProcessorEventOffset

    val code = 0x07

    def eventOffsetFor(bit: Int): Option[EROffset] = ProcessorEventOffset.offsetFor(bit)
  }

  case object PowerSupply extends SensorType {
    type EROffset = PowerSupplyEventOffset

    val code = 0x08

    def eventOffsetFor(bit: Int): Option[EROffset] = PowerSupplyEventOffset.offsetFor(bit)
  }

  case object PowerUnit extends SensorType {
    type EROffset = PowerUnitEventOffset

    val code = 0x09

    def eventOffsetFor(bit: Int): Option[EROffset] = PowerUnitEventOffset.offsetFor(bit)
  }

  case object CoolingDevice extends SensorType {
    type EROffset = Nothing

    val code = 0x0a

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Other extends SensorType {

    val code = 0x0b

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Memory extends SensorType {
    type EROffset = PowerUnitEventOffset

    val code = 0x0c

    def eventOffsetFor(bit: Int): Option[EROffset] = PowerUnitEventOffset.offsetFor(bit)
  }

  case object DriveSlotBay extends SensorType {
    type EROffset = DriveSlotBayEventOffset

    val code = 0x0d

    def eventOffsetFor(bit: Int): Option[EROffset] = DriveSlotBayEventOffset.offsetFor(bit)
  }

  case object PostMemoryResize extends SensorType {
    type EROffset = Nothing

    val code = 0x0e

    def eventOffsetFor(bit: Int): None.type = None
  }

  case object SystemFirmwareProgress extends SensorType {
    type EROffset = SystemFirmwareProgressEventOffset

    val code = 0x0f

    def eventOffsetFor(bit: Int): Option[EROffset] = SystemFirmwareProgressEventOffset.offsetFor(bit)
  }

  case object EventLoggingDisabled extends SensorType {
    type EROffset = EventLoggingDisabledEventOffset

    val code = 0x10

    def eventOffsetFor(bit: Int): Option[EROffset] = EventLoggingDisabledEventOffset.offsetFor(bit)
  }

  case object Watchdog1 extends SensorType {
    type EROffset = Watchdog1EventOffset

    val code = 0x11

    def eventOffsetFor(bit: Int): Option[EROffset] = Watchdog1EventOffset.offsetFor(bit)
  }

  case object SystemEvent extends SensorType {
    type EROffset = SystemEventEventOffset

    val code = 0x12

    def eventOffsetFor(bit: Int): Option[EROffset] = SystemEventEventOffset.offsetFor(bit)
  }

  case object CriticalInterrupt extends SensorType {
    type EROffset = CriticalInterruptEventOffset

    val code = 0x13

    def eventOffsetFor(bit: Int): Option[EROffset] = CriticalInterruptEventOffset.offsetFor(bit)
  }

  case object ButtonSwitch extends SensorType {
    type EROffset = ButtonSwitchEventOffset

    val code = 0x14

    def eventOffsetFor(bit: Int): Option[EROffset] = ButtonSwitchEventOffset.offsetFor(bit)
  }

  case object ModuleBoard extends SensorType {
    type EROffset = Nothing

    val code = 0x15

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object MicrocontrollerCoprocessor extends SensorType {
    type EROffset = Nothing

    val code = 0x16

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object AddInCard extends SensorType {
    type EROffset = Nothing

    val code = 0x17

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Chassis extends SensorType {
    type EROffset = Nothing

    val code = 0x18

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object ChipSet extends SensorType {
    type EROffset = ChipSetEventOffset

    val code = 0x19

    def eventOffsetFor(bit: Int): Option[EROffset] = ChipSetEventOffset.offsetFor(bit)
  }

  case object OtherFru extends SensorType {
    type EROffset = Nothing

    val code = 0x1a

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object CableInterconnect extends SensorType {
    type EROffset = CableInterconnectEventOffset

    val code = 0x1b

    def eventOffsetFor(bit: Int): Option[EROffset] = CableInterconnectEventOffset.offsetFor(bit)
  }

  case object Terminator extends SensorType {
    type EROffset = Nothing

    val code = 0x1c

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object SystemBootRestartInitiated extends SensorType {
    type EROffset = SystemBootRestartInitiatedEventOffset

    val code = 0x1d

    def eventOffsetFor(bit: Int): Option[EROffset] = SystemBootRestartInitiatedEventOffset.offsetFor(bit)
  }

  case object BootError extends SensorType {
    type EROffset = BootErrorEventOffset

    val code = 0x1e

    def eventOffsetFor(bit: Int): Option[EROffset] = BootErrorEventOffset.offsetFor(bit)
  }

  case object BaseOsBootInstallationStatus extends SensorType {
    type EROffset = BaseOsBootInstallationStatusEventOffset

    val code = 0x1f

    def eventOffsetFor(bit: Int): Option[EROffset] = BaseOsBootInstallationStatusEventOffset.offsetFor(bit)

  }

  case object OsStopShutdown extends SensorType {
    type EROffset = OsStopShutdownEventOffset

    val code = 0x20

    def eventOffsetFor(bit: Int): Option[EROffset] = OsStopShutdownEventOffset.offsetFor(bit)
  }

  case object SlotConnector extends SensorType {
    type EROffset = SlotConnectorEventOffset

    val code = 0x21

    def eventOffsetFor(bit: Int): Option[EROffset] = SlotConnectorEventOffset.offsetFor(bit)
  }

  case object SystemAcpiPowerState extends SensorType {
    type EROffset = SystemAcpiPowerStateEventOffset

    val code = 0x22

    def eventOffsetFor(bit: Int): Option[EROffset] = SystemAcpiPowerStateEventOffset.offsetFor(bit)
  }

  case object Watchdog2 extends SensorType {
    type EROffset = Watchdog2EventOffset

    val code = 0x23

    def eventOffsetFor(bit: Int): Option[EROffset] = Watchdog2EventOffset.offsetFor(bit)
  }

  case object PlatformAlert extends SensorType {
    type EROffset = PlatformAlertEventOffset

    val code = 0x24

    def eventOffsetFor(bit: Int): Option[EROffset] = PlatformAlertEventOffset.offsetFor(bit)
  }

  case object EntityPresence extends SensorType {
    type EROffset = EntityPresenceEventOffset

    val code = 0x25

    def eventOffsetFor(bit: Int): Option[EROffset] = EntityPresenceEventOffset.offsetFor(bit)
  }

  case object MonitorAsicIc extends SensorType {
    type EROffset = Nothing

    val code = 0x26

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }

  case object Lan extends SensorType {
    type EROffset = LanEventOffset

    val code = 0x27

    def eventOffsetFor(bit: Int): Option[EROffset] = LanEventOffset.offsetFor(bit)
  }

  case object ManagementSubsystemHealth extends SensorType {
    type EROffset = ManagementSubsystemHealthEventOffset

    val code = 0x28

    def eventOffsetFor(bit: Int): Option[EROffset] = ManagementSubsystemHealthEventOffset.offsetFor(bit)
  }

  case object Battery extends SensorType {
    type EROffset = BatteryEventOffset

    val code = 0x29

    def eventOffsetFor(bit: Int): Option[EROffset] = BatteryEventOffset.offsetFor(bit)
  }

  case object SessionAudit extends SensorType {
    type EROffset = SessionAuditEventOffset

    val code = 0x2a

    def eventOffsetFor(bit: Int): Option[EROffset] = SessionAuditEventOffset.offsetFor(bit)
  }

  case object VersionChange extends SensorType {
    type EROffset = VersionChangeEventOffset

    val code = 0x2b

    def eventOffsetFor(bit: Int): Option[EROffset] = VersionChangeEventOffset.offsetFor(bit)
  }

  case object FruState extends SensorType {
    type EROffset = FruStateEventOffset

    val code = 0x2c

    def eventOffsetFor(bit: Int): Option[EROffset] = FruStateEventOffset.offsetFor(bit)
  }

  case class Oem(code: Int) extends SensorType {
    type EROffset = Nothing

    def eventOffsetFor(bit: Int): Option[EROffset] = None
  }
}
