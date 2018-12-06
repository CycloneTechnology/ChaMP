package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait SensorType {
  def code: Int

  def eventOffsetFor(bit: Int): Option[EventReadingOffset]
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
    val code = 0x01

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Voltage extends SensorType {
    val code = 0x02

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Current extends SensorType {
    val code = 0x03

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Fan extends SensorType {
    val code = 0x04

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object PhysicalSecurity extends SensorType {
    val code = 0x05

    object PhysicalSecurityEventOffset {

      case object GeneralChassisIntrusion extends EventReadingOffset

      case object DriveBayIntrusion extends EventReadingOffset

      case object IOCardAreaIntrusion extends EventReadingOffset

      case object ProcessorAreaIntrusion extends EventReadingOffset

      case object LanLeashLost extends EventReadingOffset

      case object UnauthorizedDock extends EventReadingOffset

      case object FanAreaIntrusion extends EventReadingOffset

    }

    import PhysicalSecurityEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(GeneralChassisIntrusion)
      case 0x01 => Some(DriveBayIntrusion)
      case 0x02 => Some(IOCardAreaIntrusion)
      case 0x03 => Some(ProcessorAreaIntrusion)
      case 0x04 => Some(LanLeashLost)
      case 0x05 => Some(UnauthorizedDock)
      case 0x06 => Some(FanAreaIntrusion)
      case _    => None
    }
  }

  case object PlatformSecurity extends SensorType {
    val code = 0x06

    object PlatformSecurityEventOffset {

      case object SecureMode extends EventReadingOffset

      case object PreBootPasswordViolationUserPassword extends EventReadingOffset

      case object PreBootPasswordViolationAttemptSetupPassword extends EventReadingOffset

      case object PreBootPasswordViolationNetworkBootPassword extends EventReadingOffset

      case object OtherPreBootPasswordViolation extends EventReadingOffset

      case object OutOfBandAccessPasswordViolation extends EventReadingOffset

    }

    import PlatformSecurityEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SecureMode)
      case 0x01 => Some(PreBootPasswordViolationUserPassword)
      case 0x02 => Some(PreBootPasswordViolationAttemptSetupPassword)
      case 0x03 => Some(PreBootPasswordViolationNetworkBootPassword)
      case 0x04 => Some(OtherPreBootPasswordViolation)
      case 0x05 => Some(OutOfBandAccessPasswordViolation)
      case _    => None
    }
  }

  case object Processor extends SensorType {
    val code = 0x07

    object ProcessorEventOffset {

      case object Ierr extends EventReadingOffset

      case object ThermalTrip extends EventReadingOffset

      case object Frb1BistFailure extends EventReadingOffset

      case object Frb2HangInPostFailure extends EventReadingOffset

      case object Frb3ProcessorStartupInitializationFailure extends EventReadingOffset

      case object ConfigurationError extends EventReadingOffset

      case object SmbiosUncorrectableCpuComplexError extends EventReadingOffset

      case object ProcessorPresenceDetected extends EventReadingOffset

      case object ProcessorDisabled extends EventReadingOffset

      case object TerminatorPresenceDetected extends EventReadingOffset

      case object ProcessorAutomaticallyThrottled extends EventReadingOffset

      case object MachineCheckException extends EventReadingOffset

      case object CorrectableMachineCheckError extends EventReadingOffset

    }

    import ProcessorEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(Ierr)
      case 0x01 => Some(ThermalTrip)
      case 0x02 => Some(Frb1BistFailure)
      case 0x03 => Some(Frb2HangInPostFailure)
      case 0x04 => Some(Frb3ProcessorStartupInitializationFailure)
      case 0x05 => Some(ConfigurationError)
      case 0x06 => Some(SmbiosUncorrectableCpuComplexError)
      case 0x07 => Some(ProcessorPresenceDetected)
      case 0x08 => Some(ProcessorDisabled)
      case 0x09 => Some(TerminatorPresenceDetected)
      case 0x0a => Some(ProcessorAutomaticallyThrottled)
      case 0x0b => Some(MachineCheckException)
      case 0x0c => Some(CorrectableMachineCheckError)
      case _    => None
    }
  }

  case object PowerSupply extends SensorType {
    val code = 0x08

    object PowerSupplyEventOffset {

      case object PresenceDetected extends EventReadingOffset

      case object PowerSupplyFailureDetected extends EventReadingOffset

      case object PredictiveFailure extends EventReadingOffset

      case object PowerSupplyInputLostAcDc extends EventReadingOffset

      case object PowerSupplyInputLostOrOutOfRange extends EventReadingOffset

      case object PowerSupplyInputOutOfRangeButPresent extends EventReadingOffset

      case object ConfigurationError extends EventReadingOffset

      case object PowerSupplyInactiveInStandbyState extends EventReadingOffset

    }

    import PowerSupplyEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {

      case 0x00 => Some(PresenceDetected)
      case 0x01 => Some(PowerSupplyFailureDetected)
      case 0x02 => Some(PredictiveFailure)
      case 0x03 => Some(PowerSupplyInputLostAcDc)
      case 0x04 => Some(PowerSupplyInputLostOrOutOfRange)
      case 0x05 => Some(PowerSupplyInputOutOfRangeButPresent)
      case 0x06 => Some(ConfigurationError)
      case 0x07 => Some(PowerSupplyInactiveInStandbyState)
      case _    => None
    }
  }

  case object PowerUnit extends SensorType {
    val code = 0x09

    object PowerUnitEventOffset {

      case object PowerOffPowerDown extends EventReadingOffset

      case object PowerCycle extends EventReadingOffset

      case object PowerDown240VA extends EventReadingOffset

      case object InterlockPowerDown extends EventReadingOffset

      case object ACLostPowerInputLostThePowerSourceForThePowerUnitWasLost extends EventReadingOffset

      case object SoftPowerControlFailureUnitDidNotRespondToRequestToTurnOn extends EventReadingOffset

      case object PowerUnitFailureDetected extends EventReadingOffset

      case object PredictiveFailure extends EventReadingOffset

    }

    import PowerUnitEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PowerOffPowerDown)
      case 0x01 => Some(PowerCycle)
      case 0x02 => Some(PowerDown240VA)
      case 0x03 => Some(InterlockPowerDown)
      case 0x04 => Some(ACLostPowerInputLostThePowerSourceForThePowerUnitWasLost)
      case 0x05 => Some(SoftPowerControlFailureUnitDidNotRespondToRequestToTurnOn)
      case 0x06 => Some(PowerUnitFailureDetected)
      case 0x07 => Some(PredictiveFailure)
      case _    => None
    }
  }

  case object CoolingDevice extends SensorType {
    val code = 0x0a

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Other extends SensorType {
    val code = 0x0b

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Memory extends SensorType {
    val code = 0x0c

    object MemoryEventOffset {

      case object CorrectableEccOtherCorrectableMemoryError extends EventReadingOffset

      case object UncorrectableEccOtherUncorrectableMemoryError extends EventReadingOffset

      case object Parity extends EventReadingOffset

      case object MemoryScrubFailedStuckBit extends EventReadingOffset

      case object MemoryDeviceDisabled extends EventReadingOffset

      case object CorrectableEccOtherCorrectableMemoryErrorLoggingLimitReached extends EventReadingOffset

      case object PresenceDetected extends EventReadingOffset

      case object ConfigurationError extends EventReadingOffset

      case object Spare extends EventReadingOffset

      case object MemoryAutomaticallyThrottled extends EventReadingOffset

      case object CriticalOvertemperature extends EventReadingOffset

    }

    import MemoryEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(CorrectableEccOtherCorrectableMemoryError)
      case 0x01 => Some(UncorrectableEccOtherUncorrectableMemoryError)
      case 0x02 => Some(Parity)
      case 0x03 => Some(MemoryScrubFailedStuckBit)
      case 0x04 => Some(MemoryDeviceDisabled)
      case 0x05 => Some(CorrectableEccOtherCorrectableMemoryErrorLoggingLimitReached)
      case 0x06 => Some(PresenceDetected)
      case 0x07 => Some(ConfigurationError)
      case 0x08 => Some(Spare)
      case 0x09 => Some(MemoryAutomaticallyThrottled)
      case 0x0a => Some(CriticalOvertemperature)
      case _    => None
    }
  }

  case object DriveSlotBay extends SensorType {
    val code = 0x0d

    object DriveSlotBayEventOffset {

      case object DrivePresence extends EventReadingOffset

      case object DriveFault extends EventReadingOffset

      case object PredictiveFailure extends EventReadingOffset

      case object HotSpare extends EventReadingOffset

      case object ConsistencyCheckParityCheckInProgress extends EventReadingOffset

      case object InCriticalArray extends EventReadingOffset

      case object InFailedArray extends EventReadingOffset

      case object RebuildRemapInProgress extends EventReadingOffset

      case object RebuildRemapAbortedWasNotCompletedNormally extends EventReadingOffset

    }

    import DriveSlotBayEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(DrivePresence)
      case 0x01 => Some(DriveFault)
      case 0x02 => Some(PredictiveFailure)
      case 0x03 => Some(HotSpare)
      case 0x04 => Some(ConsistencyCheckParityCheckInProgress)
      case 0x05 => Some(InCriticalArray)
      case 0x06 => Some(InFailedArray)
      case 0x07 => Some(RebuildRemapInProgress)
      case 0x08 => Some(RebuildRemapAbortedWasNotCompletedNormally)
      case _    => None
    }
  }

  case object PostMemoryResize extends SensorType {
    val code = 0x0e

    def eventOffsetFor(bit: Int): None.type = None
  }

  case object SystemFirmwareProgress extends SensorType {
    val code = 0x0f

    object SystemFirmwareProgressEventOffset {

      case object FirmwareErrorPostError extends EventReadingOffset

      case object FirmwareHang extends EventReadingOffset

      case object FirmwareProgress extends EventReadingOffset

    }

    import SystemFirmwareProgressEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(FirmwareErrorPostError)
      case 0x01 => Some(FirmwareHang)
      case 0x02 => Some(FirmwareProgress)
      case _    => None
    }

  }

  case object EventLoggingDisabled extends SensorType {
    val code = 0x10

    object EventLoggingDisabledEventOffset {

      case object CorrectableMemoryErrorLoggingDisabled extends EventReadingOffset

      case object EventTypeLoggingDisabled extends EventReadingOffset

      case object LogAreaResetCleared extends EventReadingOffset

      case object AllEventLoggingDisabled extends EventReadingOffset

      case object SelFull extends EventReadingOffset

      case object SelAlmostFull extends EventReadingOffset

      case object CorrectableMachineCheckErrorLoggingDisabled extends EventReadingOffset

    }

    import EventLoggingDisabledEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(CorrectableMemoryErrorLoggingDisabled)
      case 0x01 => Some(EventTypeLoggingDisabled)
      case 0x02 => Some(LogAreaResetCleared)
      case 0x03 => Some(AllEventLoggingDisabled)
      case 0x04 => Some(SelFull)
      case 0x05 => Some(SelAlmostFull)
      case 0x06 => Some(CorrectableMachineCheckErrorLoggingDisabled)
      case _    => None
    }
  }

  case object Watchdog1 extends SensorType {
    val code = 0x11

    object Watchdog1EventOffset {

      case object BiosWatchdogReset extends EventReadingOffset

      case object OsWatchdogReset extends EventReadingOffset

      case object OsWatchdogShutDown extends EventReadingOffset

      case object OsWatchdogPowerDown extends EventReadingOffset

      case object OsWatchdogPowerCycle extends EventReadingOffset

      case object OsWatchdogNmiDiagnosticInterrupt extends EventReadingOffset

      case object OsWatchdogExpiredStatusOnly extends EventReadingOffset

      case object OsWatchdogPreTimeoutInterruptNonNMI extends EventReadingOffset

    }

    import Watchdog1EventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(BiosWatchdogReset)
      case 0x01 => Some(OsWatchdogReset)
      case 0x02 => Some(OsWatchdogShutDown)
      case 0x03 => Some(OsWatchdogPowerDown)
      case 0x04 => Some(OsWatchdogPowerCycle)
      case 0x05 => Some(OsWatchdogNmiDiagnosticInterrupt)
      case 0x06 => Some(OsWatchdogExpiredStatusOnly)
      case 0x07 => Some(OsWatchdogPreTimeoutInterruptNonNMI)
      case _    => None
    }
  }

  case object SystemEvent extends SensorType {
    val code = 0x12

    object SystemEventEventOffset {

      case object SystemReconfigured extends EventReadingOffset

      case object OEMSystemBootEvent extends EventReadingOffset

      case object UndeterminedSystemHardwareFailure extends EventReadingOffset

      case object EntryAddedToAuxiliaryLog extends EventReadingOffset

      case object PEFAction extends EventReadingOffset

      case object TimestampClockSynch extends EventReadingOffset

    }

    import SystemEventEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SystemReconfigured)
      case 0x01 => Some(OEMSystemBootEvent)
      case 0x02 => Some(UndeterminedSystemHardwareFailure)
      case 0x03 => Some(EntryAddedToAuxiliaryLog)
      case 0x04 => Some(PEFAction)
      case 0x05 => Some(TimestampClockSynch)
      case _    => None
    }
  }

  case object CriticalInterrupt extends SensorType {
    val code = 0x13

    object CriticalInterruptEventOffset {

      case object FrontPanelNmiDiagnosticInterrupt extends EventReadingOffset

      case object BusTimeout extends EventReadingOffset

      case object IoChannelCheckNmi extends EventReadingOffset

      case object SoftwareNmi extends EventReadingOffset

      case object PciPerr extends EventReadingOffset

      case object PciSerr extends EventReadingOffset

      case object EisaFailSafeTimeout extends EventReadingOffset

      case object BusCorrectableError extends EventReadingOffset

      case object BusUncorrectableError extends EventReadingOffset

      case object FatalNmi extends EventReadingOffset

      case object BusFatalError extends EventReadingOffset

      case object BusDegraded extends EventReadingOffset

    }

    import CriticalInterruptEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(FrontPanelNmiDiagnosticInterrupt)
      case 0x01 => Some(BusTimeout)
      case 0x02 => Some(IoChannelCheckNmi)
      case 0x03 => Some(SoftwareNmi)
      case 0x04 => Some(PciPerr)
      case 0x05 => Some(PciSerr)
      case 0x06 => Some(EisaFailSafeTimeout)
      case 0x07 => Some(BusCorrectableError)
      case 0x08 => Some(BusUncorrectableError)
      case 0x09 => Some(FatalNmi)
      case 0x0a => Some(BusFatalError)
      case 0x0b => Some(BusDegraded)
      case _    => None
    }
  }

  case object ButtonSwitch extends SensorType {
    val code = 0x14

    object ButtonSwitchEventOffset {

      case object PowerButtonPressed extends EventReadingOffset

      case object SleepButtonPressed extends EventReadingOffset

      case object ResetButtonPressed extends EventReadingOffset

      case object FruLatchOpen extends EventReadingOffset

      case object FruServiceRequestButton extends EventReadingOffset

    }

    import ButtonSwitchEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PowerButtonPressed)
      case 0x01 => Some(SleepButtonPressed)
      case 0x02 => Some(ResetButtonPressed)
      case 0x03 => Some(FruLatchOpen)
      case 0x04 => Some(FruServiceRequestButton)
      case _    => None
    }
  }

  case object ModuleBoard extends SensorType {
    val code = 0x15

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object MicrocontrollerCoprocessor extends SensorType {
    val code = 0x16

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object AddInCard extends SensorType {
    val code = 0x17

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Chassis extends SensorType {
    val code = 0x18

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object ChipSet extends SensorType {
    val code = 0x19

    object ChipSetEventOffset {

      case object SoftPowerControlFailure extends EventReadingOffset

      case object ThermalTrip extends EventReadingOffset

    }

    import ChipSetEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SoftPowerControlFailure)
      case 0x01 => Some(ThermalTrip)
      case _    => None
    }
  }

  case object OtherFru extends SensorType {
    val code = 0x1a

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object CableInterconnect extends SensorType {
    val code = 0x1b

    object CableInterconnectEventOffset {

      case object CableInterconnectIsConnected extends EventReadingOffset

      case object ConfigurationErrorIncorrectCableConnectedIncorrectInterconnection extends EventReadingOffset

    }

    import CableInterconnectEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(CableInterconnectIsConnected)
      case 0x01 => Some(ConfigurationErrorIncorrectCableConnectedIncorrectInterconnection)
      case _    => None
    }
  }

  case object Terminator extends SensorType {
    val code = 0x1c

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object SystemBootRestartInitiated extends SensorType {
    val code = 0x1d

    object SystemBootRestartInitiatedEventOffset {

      case object InitiatedByPowerUp extends EventReadingOffset

      case object InitiatedByHardReset extends EventReadingOffset

      case object InitiatedByWarmReset extends EventReadingOffset

      case object UserRequestedPXEBoot extends EventReadingOffset

      case object AutomaticBootToDiagnostic extends EventReadingOffset

      case object OSRuntimeSoftwareInitiatedHardReset extends EventReadingOffset

      case object OSRuntimeSoftwareInitiatedWarmReset extends EventReadingOffset

      case object SystemRestart extends EventReadingOffset

    }

    import SystemBootRestartInitiatedEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(InitiatedByPowerUp)
      case 0x01 => Some(InitiatedByHardReset)
      case 0x02 => Some(InitiatedByWarmReset)
      case 0x03 => Some(UserRequestedPXEBoot)
      case 0x04 => Some(AutomaticBootToDiagnostic)
      case 0x05 => Some(OSRuntimeSoftwareInitiatedHardReset)
      case 0x06 => Some(OSRuntimeSoftwareInitiatedWarmReset)
      case 0x07 => Some(SystemRestart)
      case _    => None
    }
  }

  case object BootError extends SensorType {
    val code = 0x1e

    object BootErrorEventOffset {

      case object NoBootableMedia extends EventReadingOffset

      case object NonBootableDisketteLeftInDrive extends EventReadingOffset

      case object PXEServerNotFound extends EventReadingOffset

      case object InvalidBootSector extends EventReadingOffset

      case object TimeoutWaitingForUserSelectionOfBootSource extends EventReadingOffset

    }

    import BootErrorEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(NoBootableMedia)
      case 0x01 => Some(NonBootableDisketteLeftInDrive)
      case 0x02 => Some(PXEServerNotFound)
      case 0x03 => Some(InvalidBootSector)
      case 0x04 => Some(TimeoutWaitingForUserSelectionOfBootSource)
      case _    => None
    }
  }

  case object BaseOsBootInstallationStatus extends SensorType {
    val code = 0x1f

    object BaseOsBootInstallationStatusEventOffset {

      case object ABootCompleted extends EventReadingOffset

      case object CBootCompleted extends EventReadingOffset

      case object PXEBootCompleted extends EventReadingOffset

      case object DiagnosticBootCompleted extends EventReadingOffset

      case object CdRomBootCompleted extends EventReadingOffset

      case object RomBootCompleted extends EventReadingOffset

      case object BootCompletedBootDeviceNotSpecified extends EventReadingOffset

      case object BaseOsHypervisorInstallationStarted extends EventReadingOffset

      case object BaseOsHypervisorInstallationCompleted extends EventReadingOffset

      case object BaseOsHypervisorInstallationAborted extends EventReadingOffset

      case object BaseOsHypervisorInstallationFailed extends EventReadingOffset

    }

    import BaseOsBootInstallationStatusEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(ABootCompleted)
      case 0x01 => Some(CBootCompleted)
      case 0x02 => Some(PXEBootCompleted)
      case 0x03 => Some(DiagnosticBootCompleted)
      case 0x04 => Some(CdRomBootCompleted)
      case 0x05 => Some(RomBootCompleted)
      case 0x06 => Some(BootCompletedBootDeviceNotSpecified)
      case 0x07 => Some(BaseOsHypervisorInstallationStarted)
      case 0x08 => Some(BaseOsHypervisorInstallationCompleted)
      case 0x09 => Some(BaseOsHypervisorInstallationAborted)
      case 0x0a => Some(BaseOsHypervisorInstallationFailed)
      case _    => None
    }
  }

  case object OsStopShutdown extends SensorType {
    val code = 0x20

    object OsStopShutdownEventOffset {

      case object CriticalStopDuringOsLoadInitialization extends EventReadingOffset

      case object RuntimeCriticalStop extends EventReadingOffset

      case object OSGracefulStop extends EventReadingOffset

      case object OSGracefulShutdown extends EventReadingOffset

      case object SoftShutdownInitiatedByPEF extends EventReadingOffset

      case object AgentNotResponding extends EventReadingOffset

    }

    import OsStopShutdownEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(CriticalStopDuringOsLoadInitialization)
      case 0x01 => Some(RuntimeCriticalStop)
      case 0x02 => Some(OSGracefulStop)
      case 0x03 => Some(OSGracefulShutdown)
      case 0x04 => Some(SoftShutdownInitiatedByPEF)
      case 0x05 => Some(AgentNotResponding)
      case _    => None
    }
  }

  case object SlotConnector extends SensorType {
    val code = 0x21

    object SlotConnectorEventOffset {

      case object FaultyStatusAsserted extends EventReadingOffset

      case object IdentifyStatusAsserted extends EventReadingOffset

      case object SlotConnectorDeviceInstalledAttached extends EventReadingOffset

      case object SlotConnectorReadyForDeviceInstallation extends EventReadingOffset

      case object SlotConnectorReadyForDeviceRemoval extends EventReadingOffset

      case object SlotPowerIsOff extends EventReadingOffset

      case object SlotConnectorDeviceRemovalRequest extends EventReadingOffset

      case object InterlockAsserted extends EventReadingOffset

      case object SlotIsDisabled extends EventReadingOffset

      case object SlotHoldsSpareDevice extends EventReadingOffset

    }

    import SlotConnectorEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(FaultyStatusAsserted)
      case 0x01 => Some(IdentifyStatusAsserted)
      case 0x02 => Some(SlotConnectorDeviceInstalledAttached)
      case 0x03 => Some(SlotConnectorReadyForDeviceInstallation)
      case 0x04 => Some(SlotConnectorReadyForDeviceRemoval)
      case 0x05 => Some(SlotPowerIsOff)
      case 0x06 => Some(SlotConnectorDeviceRemovalRequest)
      case 0x07 => Some(InterlockAsserted)
      case 0x08 => Some(SlotIsDisabled)
      case 0x09 => Some(SlotHoldsSpareDevice)
      case _    => None
    }
  }

  case object SystemAcpiPowerState extends SensorType {
    val code = 0x22

    object SystemAcpiPowerStateEventOffset {

      case object S0G0Working extends EventReadingOffset

      case object S1SleepingWithSystemHwProcessorContextMaintained extends EventReadingOffset

      case object S2SleepingProcessorContextLost extends EventReadingOffset

      case object S3SleepingProcessorAndHwContextLostMemoryRetained extends EventReadingOffset

      case object S4NonvolatileSleepSuspendToDisk extends EventReadingOffset

      case object S5G2SoftOff extends EventReadingOffset

      case object S4S5SoftOffParticularS4S5StateCannotBeDetermined extends EventReadingOffset

      case object G3MechanicalOff extends EventReadingOffset

      case object SleepingInAnS1S2orS3State extends EventReadingOffset

      case object G1SleepingS1S4StateCannotBeDetermined extends EventReadingOffset

      case object S5EnteredByOverride extends EventReadingOffset

      case object LegacyOnState extends EventReadingOffset

      case object LegacyOffState extends EventReadingOffset

      case object Unknown extends EventReadingOffset

    }

    import SystemAcpiPowerStateEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(S0G0Working)
      case 0x01 => Some(S1SleepingWithSystemHwProcessorContextMaintained)
      case 0x02 => Some(S2SleepingProcessorContextLost)
      case 0x03 => Some(S3SleepingProcessorAndHwContextLostMemoryRetained)
      case 0x04 => Some(S4NonvolatileSleepSuspendToDisk)
      case 0x05 => Some(S5G2SoftOff)
      case 0x06 => Some(S4S5SoftOffParticularS4S5StateCannotBeDetermined)
      case 0x07 => Some(G3MechanicalOff)
      case 0x08 => Some(SleepingInAnS1S2orS3State)
      case 0x09 => Some(G1SleepingS1S4StateCannotBeDetermined)
      case 0x0a => Some(S5EnteredByOverride)
      case 0x0b => Some(LegacyOnState)
      case 0x0c => Some(LegacyOffState)
      case 0x0e => Some(Unknown)
      case _    => None
    }
  }

  case object Watchdog2 extends SensorType {
    val code = 0x23

    object Watchdog2EventOffset {

      case object TimerExpired extends EventReadingOffset

      case object HardReset extends EventReadingOffset

      case object PowerDown extends EventReadingOffset

      case object PowerCycle extends EventReadingOffset

      case object Reserved1 extends EventReadingOffset

      case object Reserved2 extends EventReadingOffset

      case object Reserved3 extends EventReadingOffset

      case object Reserved4 extends EventReadingOffset

      case object TimerInterrupt extends EventReadingOffset

    }

    import Watchdog2EventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(TimerExpired)
      case 0x01 => Some(HardReset)
      case 0x02 => Some(PowerDown)
      case 0x03 => Some(PowerCycle)
      case 0x04 => Some(Reserved1)
      case 0x05 => Some(Reserved2)
      case 0x06 => Some(Reserved3)
      case 0x07 => Some(Reserved4)
      case 0x08 => Some(TimerInterrupt)
      case _    => None
    }
  }

  case object PlatformAlert extends SensorType {
    val code = 0x24

    object PlatformAlertEventOffset {

      case object PlatformGeneratedPage extends EventReadingOffset

      case object PlatformGeneratedLanAlert extends EventReadingOffset

      case object PlatformEventTrapGenerated extends EventReadingOffset

      case object PlatformGeneratedSnmpTrap extends EventReadingOffset

    }

    import PlatformAlertEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(PlatformGeneratedPage)
      case 0x01 => Some(PlatformGeneratedLanAlert)
      case 0x02 => Some(PlatformEventTrapGenerated)
      case 0x03 => Some(PlatformGeneratedSnmpTrap)
      case _    => None
    }
  }

  case object EntityPresence extends SensorType {
    val code = 0x25

    object EntityPresenceEventOffset {

      case object EntityPresent extends EventReadingOffset

      case object EntityAbsent extends EventReadingOffset

      case object EntityDisabled extends EventReadingOffset

    }

    import EntityPresenceEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(EntityPresent)
      case 0x01 => Some(EntityAbsent)
      case 0x02 => Some(EntityDisabled)
      case _    => None
    }
  }

  case object MonitorAsicIc extends SensorType {
    val code = 0x26

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

  case object Lan extends SensorType {
    val code = 0x27

    object LanEventOffset {

      case object LanHeartbeatLost extends EventReadingOffset

      case object LanHeartbeat extends EventReadingOffset

    }

    import LanEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(LanHeartbeatLost)
      case 0x01 => Some(LanHeartbeat)
      case _    => None
    }
  }

  case object ManagementSubsystemHealth extends SensorType {
    val code = 0x28

    object ManagementSubsystemHealthEventOffset {

      case object SensorAccessDegradedOrUnavailable extends EventReadingOffset

      case object ControllerAccessDegradedOrUnavailable extends EventReadingOffset

      case object ManagementControllerOffline extends EventReadingOffset

      case object ManagementControllerUnavailable extends EventReadingOffset

      case object SensorFailure extends EventReadingOffset

      case object FruFailure extends EventReadingOffset

    }

    import ManagementSubsystemHealthEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SensorAccessDegradedOrUnavailable)
      case 0x01 => Some(ControllerAccessDegradedOrUnavailable)
      case 0x02 => Some(ManagementControllerOffline)
      case 0x03 => Some(ManagementControllerUnavailable)
      case 0x04 => Some(SensorFailure)
      case 0x05 => Some(FruFailure)
      case _    => None
    }
  }

  case object Battery extends SensorType {
    val code = 0x29

    object BatteryEventOffset {

      case object BatteryLowPredictiveFailure extends EventReadingOffset

      case object BatteryFailed extends EventReadingOffset

      case object BatteryPresenceDetected extends EventReadingOffset

    }

    import BatteryEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(BatteryLowPredictiveFailure)
      case 0x01 => Some(BatteryFailed)
      case 0x02 => Some(BatteryPresenceDetected)
      case _    => None
    }
  }

  case object SessionAudit extends SensorType {
    val code = 0x2a

    object SessionAuditEventOffset {

      case object SessionActivated extends EventReadingOffset

      case object SessionDeactivated extends EventReadingOffset

      case object InvalidUsernameOrPassword extends EventReadingOffset

      case object InvalidPasswordDisable extends EventReadingOffset

    }

    import SessionAuditEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(SessionActivated)
      case 0x01 => Some(SessionDeactivated)
      case 0x02 => Some(InvalidUsernameOrPassword)
      case 0x03 => Some(InvalidPasswordDisable)
      case _    => None
    }
  }

  case object VersionChange extends SensorType {
    val code = 0x2b

    object VersionChangeEventOffset {

      case object HardwareChangeDetectedWithAssociatedEntity extends EventReadingOffset

      case object FirmwareOrSoftwareChangeDetectedWithAssociatedEntity extends EventReadingOffset

      case object HardwareIncompatibilityDetectedWithAssociatedEntity extends EventReadingOffset

      case object FirmwareOrSoftwareIncompatibilityDetectedWithAssociatedEntity extends EventReadingOffset

      case object EntityIsInvalidOrUnsupportedHardwareVersion extends EventReadingOffset

      case object EntityContainsInvalidOrUnsupportedFirmwareOrSoftwareVersionWithAssociatedEntity
          extends EventReadingOffset

      case object HardwareChangeDetectedWithAssociatedEntityWasSuccessful extends EventReadingOffset

      case object SoftwareOrFirmwareChangeDetectedWithAssociatedEntity extends EventReadingOffset

    }

    import VersionChangeEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(HardwareChangeDetectedWithAssociatedEntity)
      case 0x01 => Some(FirmwareOrSoftwareChangeDetectedWithAssociatedEntity)
      case 0x02 => Some(HardwareIncompatibilityDetectedWithAssociatedEntity)
      case 0x03 => Some(FirmwareOrSoftwareIncompatibilityDetectedWithAssociatedEntity)
      case 0x04 => Some(EntityIsInvalidOrUnsupportedHardwareVersion)
      case 0x05 =>
        Some(EntityContainsInvalidOrUnsupportedFirmwareOrSoftwareVersionWithAssociatedEntity)
      case 0x06 => Some(HardwareChangeDetectedWithAssociatedEntityWasSuccessful)
      case 0x07 => Some(SoftwareOrFirmwareChangeDetectedWithAssociatedEntity)
      case _    => None
    }
  }

  case object FruState extends SensorType {
    val code = 0x2c

    object FruStateEventOffset {

      case object FruNotInstalled extends EventReadingOffset

      case object FruInactive extends EventReadingOffset

      case object FruActivationRequested extends EventReadingOffset

      case object FruActivationInProgress extends EventReadingOffset

      case object FruActive extends EventReadingOffset

      case object FruDeactivationRequested extends EventReadingOffset

      case object FruDeactivationInProgress extends EventReadingOffset

      case object FruCommunicationLost extends EventReadingOffset

    }

    import FruStateEventOffset._

    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = bit match {
      case 0x00 => Some(FruNotInstalled)
      case 0x01 => Some(FruInactive)
      case 0x02 => Some(FruActivationRequested)
      case 0x03 => Some(FruActivationInProgress)
      case 0x04 => Some(FruActive)
      case 0x05 => Some(FruDeactivationRequested)
      case 0x06 => Some(FruDeactivationInProgress)
      case 0x07 => Some(FruCommunicationLost)
      case _    => None
    }
  }

  case class Oem(code: Int) extends SensorType {
    def eventOffsetFor(bit: Int): Option[EventReadingOffset] = None
  }

}
