package com.cyclone.ipmi.protocol.sdr

/**
  * A converted sensor reading with units
  */
case class SensorValue(value: Double, sensorUnits: SensorUnits) {
  def message = s"$value ${sensorUnits.abbreviation}"
}
