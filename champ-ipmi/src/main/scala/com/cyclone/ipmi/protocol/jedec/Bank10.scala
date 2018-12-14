package com.cyclone.ipmi.protocol.jedec

object Bank10 extends Manufacturer.Bank {

  protected lazy val names: Map[Byte, String] = Map(
    0x01.toByte -> "Weltronics Co. LTD",
    0x02.toByte -> "VMware, Inc.",
    0x83.toByte -> "Hewlett Packard Enterprise",
    0x04.toByte -> "INTENSO",
    0x85.toByte -> "Puya Semiconductor",
    0x86.toByte -> "MEMORFI",
    0x07.toByte -> "MSC Technologies GmbH",
    0x08.toByte -> "Txrui",
    0x89.toByte -> "SiFive, Inc.",
    0x8a.toByte -> "Spreadtrum Communications",
    0x0b.toByte -> "Paragon Technology (Shenzhen) Ltd.",
    0x8c.toByte -> "UMAX Technology",
    0x0d.toByte -> "Shenzhen Yong Sheng Technology",
    0x0e.toByte -> "SNOAMOO (Shenzhen Kai Zhuo Yue)",
    0x8f.toByte -> "Daten Tecnologia LTDA",
    0x10.toByte -> "Shenzhen XinRuiYan Electronics",
    0x91.toByte -> "Eta Compute",
    0x92.toByte -> "Energous",
    0x13.toByte -> "Raspberry Pi Trading Ltd.",
    0x94.toByte -> "Shenzhen Chixingzhe Tech Co. Ltd."
  )
}
