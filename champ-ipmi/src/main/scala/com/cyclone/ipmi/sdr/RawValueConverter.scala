package com.cyclone.ipmi.sdr

import com.cyclone.ipmi.sdr.Linearization.Linearizable

/**
  * Converter from a raw reading value to an actual value
  */
trait RawValueConverter {
  val converter: RawSensorValue => SensorValue
}

object RawValueConverter {

  val NoConversion: RawValueConverter = new RawValueConverter {
    val converter: RawSensorValue => SensorValue = {
      case RawSensorValue(v) => SensorValue(v.toDouble, SensorUnits.NoUnits)
    }
  }

  def fromReadingFactors(readingFactors: ReadingFactors,
    analogSignFormat: AnalogDataFormat,
    linearizable: Linearizable,
    sensorUnits: SensorUnits): RawValueConverter = new RawValueConverter {
    val converter: RawSensorValue => SensorValue = {
      raw: RawSensorValue =>
        val rawSigned = analogSignFormat match {
          case AnalogDataFormat.Unsigned => raw.value

          case AnalogDataFormat.OnesComplement =>
            val unsigned = raw.value
            if (unsigned < 128) unsigned else unsigned - 255

          case AnalogDataFormat.TwosComplement => raw.value.toByte.toInt
          case AnalogDataFormat.NonAnalog      => raw.value
        }

        import Math._

        import readingFactors._
        val preLinearized = (m * rawSigned + b * pow(10, bExp)) * pow(10, rExp)

        SensorValue(linearizable.evaluate(preLinearized), sensorUnits)
    }
  }
}


//case class ToleranceFormula(
//  m: Int, rExp: Int,
//  linearization: Linearization
//) extends RawToValue {
//  def valueFrom(raw: RawSensorReading) = {
//
//    import Math._
//    val preLinearized = (m * raw.toDouble / 2) * pow(10, rExp)
//
//    linearization.function(preLinearized)
//  }
//}
