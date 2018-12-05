package com.cyclone.ipmi.sdr

case class RawAccuracy(value: Double) extends AnyVal

object RawAccuracy {
  /**
    * Utility to create from accuracy figure that stored as
    * 1/100 of a percent (i.e. one ten-thousandth) and an exponent
    */
  def fromPerTenThouAndExp(perTenThou: Int, exp: Int): RawAccuracy =
  RawAccuracy(perTenThou.toDouble / 10000 * Math.pow(10, exp))

}
