package com.cyclone.ipmi.sdr.jedec

import akka.util.ByteString
import com.cyclone.ipmi.codec._

// JEDEC Manufacturer lookup
// See STANDARD MANUFACTURERS IDENTIFICATION CODE at https://www.jedec.org/
// This code is based on document JEP106AT published Spe 2016

trait ManufacturerIdentificationCode {
  val code: Byte

  def name: String
}

object ManufacturerIdentificationCode {
  val banks = List(
    Bank01.bank,
    Bank02.bank,
    Bank03.bank,
    Bank04.bank,
    Bank05.bank,
    Bank06.bank,
    Bank07.bank,
    Bank08.bank,
    Bank09.bank,
    Bank10.bank)

  def decodeUsingContinuationFlag(data: ByteString): Option[ManufacturerIdentificationCode] = {
    val bankSelection = banks.zip(data).dropWhile(_._2 == 0x7f.toByte)

    bankSelection.headOption.flatMap {
      case (bank, byte) => bank.lift(byte)
    }
  }

  def decodeUsingContinuationCount(data: ByteString): Option[ManufacturerIdentificationCode] =
    banks
      .drop(data(0).toUnsignedInt & 0x7f)
      .headOption
      .flatMap(bank => bank.lift(data(1)))
}