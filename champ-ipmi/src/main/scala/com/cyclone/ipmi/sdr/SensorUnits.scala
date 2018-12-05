package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

sealed trait RateUnit

object RateUnit {
  implicit val decoder: Decoder[Option[RateUnit]] = new Decoder[Option[RateUnit]] {

    def decode(data: ByteString): Option[RateUnit] = data(0).bits3To5.toUnsignedInt match {
      case 0 => None
      case 1 => Some(PerMicroSecond)
      case 2 => Some(PerMilliSecond)
      case 3 => Some(PerSecond)
      case 4 => Some(PerMinute)
      case 5 => Some(PerHour)
      case 6 => Some(PerDay)
    }
  }

  case object PerMicroSecond extends RateUnit

  case object PerMilliSecond extends RateUnit

  case object PerSecond extends RateUnit

  case object PerMinute extends RateUnit

  case object PerHour extends RateUnit

  case object PerDay extends RateUnit

}

sealed trait UnitCombination

object UnitCombination {
  implicit val decoder: Decoder[Option[UnitCombination]] = new Decoder[Option[UnitCombination]] {

    def decode(data: ByteString): Option[UnitCombination] = data(0).bits1To2.toUnsignedInt match {
      case 0 => None
      case 1 => Some(Divide)
      case 2 => Some(Multiply)
    }
  }

  /**
    * Modification that caters for things like 'metres per second'
    */
  case object Divide extends UnitCombination

  /**
    * Modification that caters for things like 'kilowatt hours'
    */
  case object Multiply extends UnitCombination

}

sealed trait SensorUnits

object SensorUnits {

  implicit val decoder: Decoder[SensorUnits] = new Decoder[SensorUnits] {

    def decode(data: ByteString): SensorUnits = {
      val iterator = data.iterator
      val is = iterator.asInputStream

      val sensorsUnitByte = is.readByte

      val baseUnit = is.readByte.as[SensorUnit]
      val modifierUnit = is.readByte.as[SensorUnit]

      val rateUnit = sensorsUnitByte.as[Option[RateUnit]]

      sensorsUnitByte.as[Option[UnitCombination]] match {
        case None              => Simple(baseUnit, rateUnit)
        case Some(combination) => Combined(baseUnit, modifierUnit, combination, rateUnit)
      }
    }
  }

  case class Simple(unit: SensorUnit, optRateUnit: Option[RateUnit] = None) extends SensorUnits

  case class Combined(
    baseUnit: SensorUnit,
    modifierUnit: SensorUnit,
    combination: UnitCombination,
    optRateUnit: Option[RateUnit] = None
  ) extends SensorUnits

  case class Percentage(optRateUnit: Option[RateUnit]) extends SensorUnits

  case object NoUnits extends SensorUnits

}
