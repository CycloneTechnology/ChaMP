package com.cyclone.ipmi.protocol.security

private[security] object SecurityConstants {
  val AA_RAKP_NONE: Byte = 0x0
  val AA_RAKP_HMAC_SHA1: Byte = 0x1
  val AA_RAKP_HMAC_MD5: Byte = 0x2
  val AA_RAKP_HMAC_SHA256: Byte = 0x3
  val IA_NONE: Byte = 0x0
  val IA_HMAC_SHA1_96: Byte = 0x1
  val IA_HMAC_MD5_128: Byte = 0x2
  val IA_MD5_128: Byte = 0x3
  val IA_HMAC_SHA256_128: Byte = 0x4
  val CA_NONE: Byte = 0x0
  val CA_AES_CBC128: Byte = 0x1
  val CA_XRC4_128: Byte = 0x2
  val CA_XRC4_40: Byte = 0x3
}
