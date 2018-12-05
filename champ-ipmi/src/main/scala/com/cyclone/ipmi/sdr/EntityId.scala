package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait EntityId {
  def code: Byte

  def entity: String
}

object EntityId {

  implicit val decoder: Decoder[EntityId] = new Decoder[EntityId] {
    def decode(data: ByteString): EntityId = fromCode(data(0))
  }

  case object Unspecified extends EntityId {
    val code: Byte = 0x00.toByte
    val entity = "unspecified"
  }

  case object Other extends EntityId {
    val code: Byte = 0x01.toByte
    val entity = "other"
  }

  case object Unknown extends EntityId {
    val code: Byte = 0x02.toByte
    val entity = "unknown (unspecified)"
  }

  case object Processor extends EntityId {
    val code: Byte = 0x03.toByte
    val entity = "processor"
  }

  case object DiskOrDiskBay extends EntityId {
    val code: Byte = 0x04.toByte
    val entity = "disk or disk bay"
  }

  case object PeripheralBay extends EntityId {
    val code: Byte = 0x05.toByte
    val entity = "peripheral bay"
  }

  case object SystemManagementModule extends EntityId {
    val code: Byte = 0x06.toByte
    val entity = "system management module"
  }

  case object SystemBoard extends EntityId {
    val code: Byte = 0x07.toByte

    val entity =
      "system board (main system board, may also be a processor board and/or internal expansion board)"
  }

  case object MemoryModule extends EntityId {
    val code: Byte = 0x08.toByte
    val entity = "memory module (board holding memory devices)"
  }

  case object ProcessorModule extends EntityId {
    val code: Byte = 0x09.toByte

    val entity =
      "processor module (holds processors, use this designation when processors are not mounted on system board)"
  }

  case object PowerSupply extends EntityId {
    val code: Byte = 0x0a.toByte

    val entity
      : String = "power supply (DMI refers to this as a 'power unit', but it’s used to represent a power supply). " +
    "Use this value for the main power supply (supplies) for the system."
  }

  case object AddInCard extends EntityId {
    val code: Byte = 0x0b.toByte
    val entity = "add-in card"
  }

  case object FrontPanelBoard extends EntityId {
    val code: Byte = 0x0c.toByte
    val entity = "front panel board (control panel)"
  }

  case object BackPanelBoard extends EntityId {
    val code: Byte = 0x0d.toByte
    val entity = "back panel board"
  }

  case object PowerSystemBoard extends EntityId {
    val code: Byte = 0x0e.toByte
    val entity = "power system board"
  }

  case object DriveBackplane extends EntityId {
    val code: Byte = 0x0f.toByte
    val entity = "drive backplane"
  }

  case object SystemInternalExpansionBoard extends EntityId {
    val code: Byte = 0x10.toByte
    val entity = "system internal expansion board (contains expansion slots)."
  }

  case object OtherSystemBoard extends EntityId {
    val code: Byte = 0x11.toByte
    val entity = "Other system board (part of board set)"
  }

  case object ProcessorBoard extends EntityId {
    val code: Byte = 0x12.toByte

    val entity =
      "processor board (holds 1 or more processors - includes boards that hold SECC modules)"
  }

  case object PowerUnitPowerDomain extends EntityId {
    val code: Byte = 0x13.toByte

    val entity: String = "power unit / power domain - This Entity ID is typically used as a pre " +
    "- defined logical entity for grouping power supplies and/or sensors that are associated " +
    "in monitoring a particular logical power domain."
  }

  case object PowerModuleDcDcConverter extends EntityId {
    val code: Byte = 0x14.toByte

    val entity: String = "power module / DC-to-DC converter - Use this value for internal converters. " +
    "Note: You should use Entity ID 10 (power supply) for the main power supply even if the " +
    "main supply is a DC-to-DC converter, e.g. gets external power from a -48 DC source"
  }

  case object PowerManagementPowerDistributionBoard extends EntityId {
    val code: Byte = 0x15.toByte
    val entity = "power management / power distribution board"
  }

  case object ChassisBackPanelBoard extends EntityId {
    val code: Byte = 0x16.toByte
    val entity = "chassis back panel board"
  }

  case object SystemChassis extends EntityId {
    val code: Byte = 0x17.toByte
    val entity = "system chassis"
  }

  case object SubChassis extends EntityId {
    val code: Byte = 0x18.toByte
    val entity = "sub-chassis"
  }

  case object OtherChassisBoard extends EntityId {
    val code: Byte = 0x19.toByte
    val entity = "Other chassis board"
  }

  case object DiskDriveBay extends EntityId {
    val code: Byte = 0x1a.toByte
    val entity = "Disk Drive Bay"
  }

  case object PeripheralBay01 extends EntityId {
    val code: Byte = 0x1b.toByte
    val entity = "Peripheral Bay"
  }

  case object DeviceBay extends EntityId {
    val code: Byte = 0x1c.toByte
    val entity = "Device Bay"
  }

  case object FanCoolingDevice extends EntityId {
    val code: Byte = 0x1d.toByte
    val entity = "fan / cooling device"
  }

  case object CoolingUnitCoolingDomain extends EntityId {
    val code: Byte = 0x1e.toByte

    val entity: String = "cooling unit / cooling domain - This Entity ID can be used as a pre-defined " +
    "logical entity for grouping fans or other cooling devices and/or sensors that are " +
    "associated in monitoring a particular logical cooling domain."
  }

  case object CableInterconnect extends EntityId {
    val code: Byte = 0x1f.toByte
    val entity = "cable / interconnect"
  }

  case object MemoryDevice extends EntityId {
    val code: Byte = 0x20.toByte

    val entity
      : String = "memory device - This Entity ID should be used for replaceable memory devices, e.g. DIMM/SIMM. " +
    "It is recommended that Entity IDs not be used for individual non-replaceable memory devices. " +
    "Rather, monitoring and error reporting should be associated with the FRU [e.g. memory card] holding the memory."
  }

  case object SystemManagementSoftware extends EntityId {
    val code: Byte = 0x21.toByte
    val entity = "System Management Software"
  }

  case object SystemFirmware extends EntityId {
    val code: Byte = 0x22.toByte
    val entity = "System Firmware (e.g. BIOS / EFI)"
  }

  case object OperatingSystem extends EntityId {
    val code: Byte = 0x23.toByte
    val entity = "Operating System"
  }

  case object SystemBus extends EntityId {
    val code: Byte = 0x24.toByte
    val entity = "system bus"
  }

  case object Group extends EntityId {
    val code: Byte = 0x25.toByte

    val entity: String = "Group - This is a logical entity for use with Entity Association records. " +
    "It is provided to allow an Entity-association record to define a grouping of entities " +
    "when there is no appropriate pre-defined entity for the container entity. " +
    "This Entity should not be used as a physical entity."
  }

  case object RemoteOobManagementCommunicationDevice extends EntityId {
    val code: Byte = 0x26.toByte
    val entity = "Remote (Out of Band) Management Communication Device"
  }

  case object ExternalEnvironment extends EntityId {
    val code: Byte = 0x27.toByte

    val entity: String = "External Environment - This Entity ID can be used to identify the environment " +
    "outside the system chassis. For example, a system may have a temperature sensor that " +
    "monitors the temperature “outside the box”. Such a temperature sensor can be associated " +
    "with an External Environment entity. This value will typically be used as a single instance physical entity. " +
    "However, the Entity Instance value can be used to denote a difference in regions of the " +
    "external environment. For example, the region around the front of a chassis may be " +
    "considered to be different from the region around the back, in which case it " +
    "would be reasonable to have two different instances of the External Environment entity."
  }

  case object Battery extends EntityId {
    val code: Byte = 0x28.toByte
    val entity = "battery"
  }

  case object ProcessingBlade extends EntityId {
    val code: Byte = 0x29.toByte

    val entity: String = "Processing blade (a blade module that contains processor, memory, " +
    "and I/O connections that enable it to operate as a processing entity)"
  }

  case object ConnectivitySwitch extends EntityId {
    val code: Byte = 0x2a.toByte

    val entity: String = "Connectivity switch (a blade module that provides the fabric or network connection " +
    "for one or more processing blades or modules)"
  }

  case object ProcessorMemoryModule extends EntityId {
    val code: Byte = 0x2b.toByte
    val entity = "Processor/memory module (processor and memory together on a module)"
  }

  case object IoModule extends EntityId {
    val code: Byte = 0x2c.toByte
    val entity = "I/O module (a module that contains the main elements of an I/O interface)"
  }

  case object ProcessorIoModule extends EntityId {
    val code: Byte = 0x2d.toByte
    val entity = "Processor/ IO module (a combination processor and i/O module)"
  }

  case object ManagementControllerFirmware extends EntityId {
    val code: Byte = 0x2e.toByte

    val entity =
      "Management Controller Firmware (Represents firmware or software running on a management controller)"
  }

  case object IpmiChannel extends EntityId {
    val code: Byte = 0x2f.toByte

    val entity: String = "IPMI Channel - This Entity ID enables associating sensors with the IPMI " +
    "communication channels - for example a Redundancy sensor could be used to report redundancy " +
    "status for a channel that is composed of multiple physical links. " +
    "By convention, the Entity Instance corresponds to the channel number"
  }

  case object PciBus extends EntityId {
    val code: Byte = 0x30.toByte
    val entity = "PCI Bus"
  }

  case object PciExpressBus extends EntityId {
    val code: Byte = 0x31.toByte
    val entity = "PCI Express™ Bus"
  }

  case object ScsiBusParalell extends EntityId {
    val code: Byte = 0x32.toByte
    val entity = "SCSI Bus (parallel)"
  }

  case object SataSasBus extends EntityId {
    val code: Byte = 0x33.toByte
    val entity = "SATA / SAS bus"
  }

  case object ProcessorFrontSideBus extends EntityId {
    val code: Byte = 0x34.toByte
    val entity = "Processor / front-side bus"
  }

  case object RealTimeClock extends EntityId {
    val code: Byte = 0x35.toByte
    val entity = "Real Time Clock (RTC)"
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware00 extends EntityId {
    val code: Byte = 0x36.toByte

    val entity: String = "reserved. This value was previously a duplicate of 22h (System Firmware). " +
    "This value should remain reserved for any future versions of the specification to " +
    "avoid conflicts with older applications that may interpret this as System Firmware."
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware01 extends EntityId {
    val code: Byte = 0x37.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware02 extends EntityId {
    val code: Byte = 0x38.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware03 extends EntityId {
    val code: Byte = 0x39.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware04 extends EntityId {
    val code: Byte = 0x3a.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware05 extends EntityId {
    val code: Byte = 0x3b.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware06 extends EntityId {
    val code: Byte = 0x3c.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware07 extends EntityId {
    val code: Byte = 0x3d.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware08 extends EntityId {
    val code: Byte = 0x3e.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object ReservedPreviouslyDuplicateOfSystemFirmware09 extends EntityId {
    val code: Byte = 0x3f.toByte
    val entity: String = ReservedPreviouslyDuplicateOfSystemFirmware00.entity
  }

  case object AirInlet extends EntityId {
    val code: Byte = 0x40.toByte

    val entity: String = "air inlet - This Entity ID enables associating sensors such as temperature to the " +
    "airflow at an air inlet. This Entity ID value is equivalent to Entity ID 37h. " +
    "It is provided for interoperability with the DCMI 1.0 specifications."
  }

  case object ProcessorCpu extends EntityId {
    val code: Byte = 0x41.toByte

    val entity: String = "processor / CPU - This Entity ID value is equivalent to Entity ID 03h (processor). " +
    "It is provided for interoperability with the DCMI 1.0 specifications."
  }

  case object Baseboard extends EntityId {
    val code: Byte = 0x42.toByte

    val entity
      : String = "baseboard / main system board - This Entity ID value is equivalent to Entity ID 07h (system board). " +
    "It is provided for interoperability with the DCMI 1.0 specifications."
  }

  case class ChassicSpecific(code: Byte) extends EntityId {
    val entity = "Chassis-specific entity."
  }

  case class BoardSetSpecific(code: Byte) extends EntityId {
    val entity = "Board-set specific entity."
  }

  case class Oem(code: Byte) extends EntityId {
    val entity = "Assigned by the system integrator, or OEM."
  }

  def fromCode(code: Byte): EntityId = code match {
    case Unspecified.code                            => Unspecified
    case Other.code                                  => Other
    case Unknown.code                                => Unknown
    case Processor.code                              => Processor
    case DiskOrDiskBay.code                          => DiskOrDiskBay
    case PeripheralBay.code                          => PeripheralBay
    case SystemManagementModule.code                 => SystemManagementModule
    case SystemBoard.code                            => SystemBoard
    case MemoryModule.code                           => MemoryModule
    case ProcessorModule.code                        => ProcessorModule
    case PowerSupply.code                            => PowerSupply
    case AddInCard.code                              => AddInCard
    case FrontPanelBoard.code                        => FrontPanelBoard
    case BackPanelBoard.code                         => BackPanelBoard
    case PowerSystemBoard.code                       => PowerSystemBoard
    case DriveBackplane.code                         => DriveBackplane
    case SystemInternalExpansionBoard.code           => SystemInternalExpansionBoard
    case OtherSystemBoard.code                       => OtherSystemBoard
    case ProcessorBoard.code                         => ProcessorBoard
    case PowerUnitPowerDomain.code                   => PowerUnitPowerDomain
    case PowerModuleDcDcConverter.code               => PowerModuleDcDcConverter
    case PowerManagementPowerDistributionBoard.code  => PowerManagementPowerDistributionBoard
    case ChassisBackPanelBoard.code                  => ChassisBackPanelBoard
    case SystemChassis.code                          => SystemChassis
    case SubChassis.code                             => SubChassis
    case OtherChassisBoard.code                      => OtherChassisBoard
    case DiskDriveBay.code                           => DiskDriveBay
    case PeripheralBay01.code                        => PeripheralBay01
    case DeviceBay.code                              => DeviceBay
    case FanCoolingDevice.code                       => FanCoolingDevice
    case CoolingUnitCoolingDomain.code               => CoolingUnitCoolingDomain
    case CableInterconnect.code                      => CableInterconnect
    case MemoryDevice.code                           => MemoryDevice
    case SystemManagementSoftware.code               => SystemManagementSoftware
    case SystemFirmware.code                         => SystemFirmware
    case OperatingSystem.code                        => OperatingSystem
    case SystemBus.code                              => SystemBus
    case Group.code                                  => Group
    case RemoteOobManagementCommunicationDevice.code => RemoteOobManagementCommunicationDevice
    case ExternalEnvironment.code                    => ExternalEnvironment
    case Battery.code                                => Battery
    case ProcessingBlade.code                        => ProcessingBlade
    case ConnectivitySwitch.code                     => ConnectivitySwitch
    case ProcessorMemoryModule.code                  => ProcessorMemoryModule
    case IoModule.code                               => IoModule
    case ProcessorIoModule.code                      => ProcessorIoModule
    case ManagementControllerFirmware.code           => ManagementControllerFirmware
    case IpmiChannel.code                            => IpmiChannel
    case PciBus.code                                 => PciBus
    case PciExpressBus.code                          => PciExpressBus
    case ScsiBusParalell.code                        => ScsiBusParalell
    case SataSasBus.code                             => SataSasBus
    case ProcessorFrontSideBus.code                  => ProcessorFrontSideBus
    case RealTimeClock.code                          => RealTimeClock
    case ReservedPreviouslyDuplicateOfSystemFirmware00.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware00
    case ReservedPreviouslyDuplicateOfSystemFirmware01.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware01
    case ReservedPreviouslyDuplicateOfSystemFirmware02.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware02
    case ReservedPreviouslyDuplicateOfSystemFirmware03.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware03
    case ReservedPreviouslyDuplicateOfSystemFirmware04.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware04
    case ReservedPreviouslyDuplicateOfSystemFirmware05.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware05
    case ReservedPreviouslyDuplicateOfSystemFirmware06.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware06
    case ReservedPreviouslyDuplicateOfSystemFirmware07.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware07
    case ReservedPreviouslyDuplicateOfSystemFirmware08.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware08
    case ReservedPreviouslyDuplicateOfSystemFirmware09.code =>
      ReservedPreviouslyDuplicateOfSystemFirmware09
    case AirInlet.code           => AirInlet
    case ProcessorCpu.code       => ProcessorCpu
    case Baseboard.code          => Baseboard
    case b if b.in(0x90 to 0xaf) => ChassicSpecific(b)
    case b if b.in(0xb0 to 0xcf) => BoardSetSpecific(b)
    case b if b.in(0xd0 to 0xff) => Oem(b)
  }
}
