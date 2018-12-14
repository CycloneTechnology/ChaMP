package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

trait DeviceTypeModifier

object DeviceTypeModifier {

  case object DimmMemoryId extends DeviceTypeModifier

  case object IpmiFruInventory extends DeviceTypeModifier

  case object SystemProcessorCartridgeFruOrPIROM extends DeviceTypeModifier

  case object Reserved extends DeviceTypeModifier

  case object Heceta1 extends DeviceTypeModifier

  case object Heceta2 extends DeviceTypeModifier

  case object Lm80 extends DeviceTypeModifier

  case object Heceta3 extends DeviceTypeModifier

  case object Heceta4 extends DeviceTypeModifier

  case object Heceta5 extends DeviceTypeModifier

  def standardTypeModifierFor(bit: Int): Option[DeviceTypeModifier] = bit match {
    case 0x00 => None
    case 0x01 => Some(DeviceTypeModifier.DimmMemoryId)
    case 0x02 => Some(DeviceTypeModifier.IpmiFruInventory)
    case 0x03 => Some(DeviceTypeModifier.SystemProcessorCartridgeFruOrPIROM)
    case _    => Some(DeviceTypeModifier.Reserved)
  }
}

/**
  * Indicates the type of reading and/or events that the sensor supports.
  * Table 42-1
  */
sealed trait DeviceType {
  val code: Byte

  def deviceType: String

  def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = None
}

object DeviceType {
  implicit val decoder: Decoder[DeviceType] = new Decoder[DeviceType] {

    def decode(data: ByteString): DeviceType =
      data(0) match {
        case Reserved00.code => Reserved00
        case Reserved01.code => Reserved01
        case DS1624TemperatureSensorEEPROMorEquivalent.code =>
          DS1624TemperatureSensorEEPROMorEquivalent
        case DS1621TemperatureSensorOrEquivalent.code => DS1621TemperatureSensorOrEquivalent
        case LM75TemperatureSensorOrEquivalent.code   => LM75TemperatureSensorOrEquivalent
        case HecetaASICorSimilar.code                 => HecetaASICorSimilar
        case Reserved06.code                          => Reserved06
        case Reserved07.code                          => Reserved07
        case EEPROM24C01orEquivalent.code             => EEPROM24C01orEquivalent
        case EEPROM24C02orEquivalent.code             => EEPROM24C02orEquivalent
        case EEPROM24C04orEquivalent.code             => EEPROM24C04orEquivalent
        case EEPROM24C08orEquivalent.code             => EEPROM24C08orEquivalent
        case EEPROM24C16orEquivalent.code             => EEPROM24C16orEquivalent
        case EEPROM24C17orEquivalent.code             => EEPROM24C17orEquivalent
        case EEPROM24C32orEquivalent.code             => EEPROM24C32orEquivalent
        case EEPROM24C64orEquivalent.code             => EEPROM24C64orEquivalent
        case FruInventoryDeviceBehindMc.code          => FruInventoryDeviceBehindMc
        case Reserved11.code                          => Reserved11
        case Reserved12.code                          => Reserved12
        case Reserved13.code                          => Reserved13
        case PCF8570_256ByteRamOrEquivalent.code      => PCF8570_256ByteRamOrEquivalent
        case PCF8573ClockCalendar.code                => PCF8573ClockCalendar
        case PCF8574AIoPort.code                      => PCF8574AIoPort
        case PCF8583ClockCalendar.code                => PCF8583ClockCalendar
        case PCF8593ClockCalendar.code                => PCF8593ClockCalendar
        case ClockCalendar.code                       => ClockCalendar
        case PCF8591AdDaConverter.code                => PCF8591AdDaConverter
        case IoPort.code                              => IoPort
        case AdConverter.code                         => AdConverter
        case DaConverter.code                         => DaConverter
        case AdDaConverter.code                       => AdDaConverter
        case LCDControllerDriver.code                 => LCDControllerDriver
        case CoreLogicChipSetDevice.code              => CoreLogicChipSetDevice
        case LMC6874IntelligentBatteryControllerOrEquivalent.code =>
          LMC6874IntelligentBatteryControllerOrEquivalent
        case IntelligentBatteryController.code => IntelligentBatteryController
        case ComboManagementASIC.code          => ComboManagementASIC
        case Maxim1617TemperatureSensor.code   => Maxim1617TemperatureSensor
        case OtherUnspecifiedDevice.code       => OtherUnspecifiedDevice

        case b if b.in(0xc0 to 0xfff) => OemRange(b)
      }
  }

  case object Reserved00 extends DeviceType {
    val code: Byte = 0x00.toByte

    val deviceType = "reserved"
  }

  case object Reserved01 extends DeviceType {
    val code: Byte = 0x01.toByte

    val deviceType = "reserved"
  }

  case object DS1624TemperatureSensorEEPROMorEquivalent extends DeviceType {
    val code: Byte = 0x02.toByte

    val deviceType = "DS1624 temperature sensor / EEPROM or equivalent"
  }

  case object DS1621TemperatureSensorOrEquivalent extends DeviceType {
    val code: Byte = 0x03.toByte

    val deviceType = "DS1621 temperature sensor or equivalent"
  }

  case object LM75TemperatureSensorOrEquivalent extends DeviceType {
    val code: Byte = 0x04.toByte

    val deviceType = "LM75 Temperature Sensor or equivalent"
  }

  case object HecetaASICorSimilar extends DeviceType {
    val code: Byte = 0x05.toByte

    val deviceType = "'Heceta’ ASIC or similar"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = bit match {
      case 0x00 => Some(DeviceTypeModifier.Heceta1)
      case 0x01 => Some(DeviceTypeModifier.Heceta1)
      case 0x02 => Some(DeviceTypeModifier.Lm80)
      case 0x03 => Some(DeviceTypeModifier.Heceta3)
      case 0x04 => Some(DeviceTypeModifier.Heceta4)
      case 0x05 => Some(DeviceTypeModifier.Heceta5)
      case _    => None
    }
  }

  case object Reserved06 extends DeviceType {
    val code: Byte = 0x06.toByte

    val deviceType = "reserved"
  }

  case object Reserved07 extends DeviceType {
    val code: Byte = 0x07.toByte

    val deviceType = "reserved"
  }

  case object EEPROM24C01orEquivalent extends DeviceType {
    val code: Byte = 0x08.toByte

    val deviceType = "EEPROM, 24C01 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C02orEquivalent extends DeviceType {
    val code: Byte = 0x09.toByte

    val deviceType = "EEPROM, 24C02 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C04orEquivalent extends DeviceType {
    val code: Byte = 0x0a.toByte

    val deviceType = "EEPROM, 24C04 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C08orEquivalent extends DeviceType {
    val code: Byte = 0x0b.toByte

    val deviceType = "EEPROM, 24C08 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C16orEquivalent extends DeviceType {
    val code: Byte = 0x0c.toByte

    val deviceType = "EEPROM, 24C16 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C17orEquivalent extends DeviceType {
    val code: Byte = 0x0d.toByte

    val deviceType = "EEPROM, 24C17 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C32orEquivalent extends DeviceType {
    val code: Byte = 0x0e.toByte

    val deviceType = "EEPROM, 24C32 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object EEPROM24C64orEquivalent extends DeviceType {
    val code: Byte = 0x0f.toByte

    val deviceType = "EEPROM, 24C64 or equivalent"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = DeviceTypeModifier.standardTypeModifierFor(bit)
  }

  case object FruInventoryDeviceBehindMc extends DeviceType {
    val code: Byte = 0x10.toByte

    val deviceType = "FRU Inventory Device behind management controller"

    override def typeModifierFor(bit: Int): Option[DeviceTypeModifier] = bit match {
      case 0x00 => Some(DeviceTypeModifier.IpmiFruInventory)
      case 0x01 => Some(DeviceTypeModifier.DimmMemoryId)
      case 0x02 => Some(DeviceTypeModifier.IpmiFruInventory)
      case 0x03 => Some(DeviceTypeModifier.SystemProcessorCartridgeFruOrPIROM)
      case 0xff => None
      case _    => Some(DeviceTypeModifier.Reserved)
    }
  }

  case object Reserved11 extends DeviceType {
    val code: Byte = 0x11.toByte

    val deviceType = "reserved"
  }

  case object Reserved12 extends DeviceType {
    val code: Byte = 0x12.toByte

    val deviceType = "reserved"
  }

  case object Reserved13 extends DeviceType {
    val code: Byte = 0x13.toByte

    val deviceType = "reserved"
  }

  case object PCF8570_256ByteRamOrEquivalent extends DeviceType {
    val code: Byte = 0x14.toByte

    val deviceType = "PCF 8570 256 byte RAM or equivalent"
  }

  case object PCF8573ClockCalendar extends DeviceType {
    val code: Byte = 0x15.toByte

    val deviceType = "PCF 8573 clock/calendar or equivalent"
  }

  case object PCF8574AIoPort extends DeviceType {
    val code: Byte = 0x16.toByte

    val deviceType = "PCF 8574A ‘i/o port’ or equivalent"
  }

  case object PCF8583ClockCalendar extends DeviceType {
    val code: Byte = 0x17.toByte

    val deviceType = "PCF 8583 clock/calendar or equivalent"
  }

  case object PCF8593ClockCalendar extends DeviceType {
    val code: Byte = 0x18.toByte

    val deviceType = "PCF 8593 clock/calendar or equivalent"
  }

  case object ClockCalendar extends DeviceType {
    val code: Byte = 0x19.toByte

    val deviceType = "Clock calendar, type not specified"
  }

  case object PCF8591AdDaConverter extends DeviceType {
    val code: Byte = 0x1a.toByte

    val deviceType = "PCF 8591 A/D, D/A Converter or equivalent"
  }

  case object IoPort extends DeviceType {
    val code: Byte = 0x1b.toByte

    val deviceType = "i/o port, specific device not specified"
  }

  case object AdConverter extends DeviceType {
    val code: Byte = 0x1c.toByte

    val deviceType = "A/D Converter, specific device not specified"
  }

  case object DaConverter extends DeviceType {
    val code: Byte = 0x1d.toByte

    val deviceType = "D/A Converter, specific device not specified"
  }

  case object AdDaConverter extends DeviceType {
    val code: Byte = 0x1e.toByte

    val deviceType = "A/D, D/A Converter, specific device not specified"
  }

  case object LCDControllerDriver extends DeviceType {
    val code: Byte = 0x1f.toByte

    val deviceType = "LCD controller / Driver, specific device not specified"
  }

  case object CoreLogicChipSetDevice extends DeviceType {
    val code: Byte = 0x20.toByte

    val deviceType = "Core Logic (Chip set) Device, specific device not specified"
  }

  case object LMC6874IntelligentBatteryControllerOrEquivalent extends DeviceType {
    val code: Byte = 0x21.toByte

    val deviceType = "LMC6874 Intelligent Battery controller, or equivalent"
  }

  case object IntelligentBatteryController extends DeviceType {
    val code: Byte = 0x22.toByte

    val deviceType = "Intelligent Battery controller, specific device not specified"
  }

  case object ComboManagementASIC extends DeviceType {
    val code: Byte = 0x23.toByte

    val deviceType = "Combo Management ASIC, specific device not specified"
  }

  case object Maxim1617TemperatureSensor extends DeviceType {
    val code: Byte = 0x24.toByte

    val deviceType = "Maxim 1617 Temperature Sensor"
  }

  case object OtherUnspecifiedDevice extends DeviceType {
    val code: Byte = 0xbf.toByte

    val deviceType = "Other / unspecified device"
  }

  case class OemRange(code: Byte) extends DeviceType {
    val deviceType = "OEM specified device"
  }

}
