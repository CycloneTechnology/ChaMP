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
}

object ChassisType {

  implicit val decoder: Decoder[ChassisType] = new Decoder[ChassisType] {
    def decode(data: ByteString): ChassisType = fromCode(data(0))
  }

  case object Other extends ChassisType {
    val code: Byte = 0x01.toByte
  }

  case object Unknown extends ChassisType {
    val code: Byte = 0x02.toByte
  }

  case object Desktop extends ChassisType {
    val code: Byte = 0x03.toByte
  }

  case object LowProfileDesktop extends ChassisType {
    val code: Byte = 0x04.toByte
  }

  case object PizzaBox extends ChassisType {
    val code: Byte = 0x05.toByte
  }

  case object MiniTower extends ChassisType {
    val code: Byte = 0x06.toByte
  }

  case object Tower extends ChassisType {
    val code: Byte = 0x07.toByte
  }

  case object Portable extends ChassisType {
    val code: Byte = 0x08.toByte
  }

  case object Laptop extends ChassisType {
    val code: Byte = 0x09.toByte
  }

  case object Notebook extends ChassisType {
    val code: Byte = 0x0a.toByte
  }

  case object HandHeld extends ChassisType {
    val code: Byte = 0x0b.toByte
  }

  case object DockingStation extends ChassisType {
    val code: Byte = 0x0c.toByte
  }

  case object AllInOne extends ChassisType {
    val code: Byte = 0x0d.toByte
  }

  case object SubNotebook extends ChassisType {
    val code: Byte = 0x0e.toByte
  }

  case object SpaceSaving extends ChassisType {
    val code: Byte = 0x0f.toByte
  }

  case object LunchBox extends ChassisType {
    val code: Byte = 0x10.toByte
  }

  case object MainServerChassis extends ChassisType {
    val code: Byte = 0x11.toByte
  }

  case object ExpansionChassis extends ChassisType {
    val code: Byte = 0x12.toByte
  }

  case object SubChassis extends ChassisType {
    val code: Byte = 0x13.toByte
  }

  case object BusExpansionChassis extends ChassisType {
    val code: Byte = 0x14.toByte
  }

  case object PeripheralChassis extends ChassisType {
    val code: Byte = 0x15.toByte
  }

  case object RaidChassis extends ChassisType {
    val code: Byte = 0x16.toByte
  }

  case object RackMountChassis extends ChassisType {
    val code: Byte = 0x17.toByte
  }

  case object SealedCasePC extends ChassisType {
    val code: Byte = 0x18.toByte
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
