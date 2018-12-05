package com.cyclone.ipmi.fru

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/*
 * System Enclosure or Chassis Types
 * 
 * Taken from the DMTF System Management BIOS Reference Specification
 */
sealed trait ChassisType {
  def code: Byte

  // TODO remove: unused? etc...
  def description: String
}

object ChassisType {

  implicit val decoder: Decoder[ChassisType] = new Decoder[ChassisType] {
    def decode(data: ByteString): ChassisType = fromCode(data(0))
  }

  case object Other extends ChassisType {
    val code: Byte = 0x01.toByte
    val description = "Other"
  }

  case object Unknown extends ChassisType {
    val code: Byte = 0x02.toByte
    val description = "Unknown"
  }

  case object Desktop extends ChassisType {
    val code: Byte = 0x03.toByte
    val description = "Desktop"
  }

  case object LowProfileDesktop extends ChassisType {
    val code: Byte = 0x04.toByte
    val description = "Low Profile Desktop"
  }

  case object PizzaBox extends ChassisType {
    val code: Byte = 0x05.toByte
    val description = "Pizza Box"
  }

  case object MiniTower extends ChassisType {
    val code: Byte = 0x06.toByte
    val description = "Mini Tower"
  }

  case object Tower extends ChassisType {
    val code: Byte = 0x07.toByte
    val description = "Tower"
  }

  case object Portable extends ChassisType {
    val code: Byte = 0x08.toByte
    val description = "Portable"
  }

  case object Laptop extends ChassisType {
    val code: Byte = 0x09.toByte
    val description = "Laptop"
  }

  case object Notebook extends ChassisType {
    val code: Byte = 0x0a.toByte
    val description = "Notebook"
  }

  case object HandHeld extends ChassisType {
    val code: Byte = 0x0b.toByte
    val description = "Hand Held"
  }

  case object DockingStation extends ChassisType {
    val code: Byte = 0x0c.toByte
    val description = "Docking Station"
  }

  case object AllInOne extends ChassisType {
    val code: Byte = 0x0d.toByte
    val description = "All in One"
  }

  case object SubNotebook extends ChassisType {
    val code: Byte = 0x0e.toByte
    val description = "Sub Notebook"
  }

  case object SpaceSaving extends ChassisType {
    val code: Byte = 0x0f.toByte
    val description = "Space-saving"
  }

  case object LunchBox extends ChassisType {
    val code: Byte = 0x10.toByte
    val description = "Lunch Box"
  }

  case object MainServerChassis extends ChassisType {
    val code: Byte = 0x11.toByte
    val description = "Main Server Chassis"
  }

  case object ExpansionChassis extends ChassisType {
    val code: Byte = 0x12.toByte
    val description = "Expansion Chassis"
  }

  case object SubChassis extends ChassisType {
    val code: Byte = 0x13.toByte
    val description = "SubChassis"
  }

  case object BusExpansionChassis extends ChassisType {
    val code: Byte = 0x14.toByte
    val description = "Bus Expansion Chassis"
  }

  case object PeripheralChassis extends ChassisType {
    val code: Byte = 0x15.toByte
    val description = "Peripheral Chassis"
  }

  case object RaidChassis extends ChassisType {
    val code: Byte = 0x16.toByte
    val description = "RAID Chassis"
  }

  case object RackMountChassis extends ChassisType {
    val code: Byte = 0x17.toByte
    val description = "Rack Mount Chassis"
  }

  case object SealedCasePC extends ChassisType {
    val code: Byte = 0x18.toByte
    val description = "Sealed-case PC"
  }

  def fromCode(code: Byte): ChassisType = code match {
    case Other.code               => Other
    case Unknown.code             => Unknown
    case Desktop.code             => Desktop
    case LowProfileDesktop.code   => LowProfileDesktop
    case PizzaBox.code            => PizzaBox
    case MiniTower.code           => MiniTower
    case Tower.code               => Tower
    case Portable.code            => Portable
    case Laptop.code              => Laptop
    case Notebook.code            => Notebook
    case HandHeld.code            => HandHeld
    case DockingStation.code      => DockingStation
    case AllInOne.code            => AllInOne
    case SubNotebook.code         => SubNotebook
    case SpaceSaving.code         => SpaceSaving
    case LunchBox.code            => LunchBox
    case MainServerChassis.code   => MainServerChassis
    case ExpansionChassis.code    => ExpansionChassis
    case SubChassis.code          => SubChassis
    case BusExpansionChassis.code => BusExpansionChassis
    case PeripheralChassis.code   => PeripheralChassis
    case RaidChassis.code         => RaidChassis
    case RackMountChassis.code    => RackMountChassis
    case SealedCasePC.code        => SealedCasePC
  }
}
  