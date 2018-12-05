package com.cyclone.util

import org.apache.commons.codec.binary.Base64

/**
  * Base64 related utilities
  *
  * @author Jeremy.Stone
  */
object Base64Utils {

  /**
    * Encodes binary data to a (non-chunked) base64 string
    *
    * @param a_data the data to encode
    * @return the string
    */
  def encodeBase64(a_data: Array[Byte]): String =
    org.apache.commons.codec.binary.StringUtils.newStringUtf8(Base64.encodeBase64(a_data, false))

  /**
    * Encodes binary data from a (non-chunked) base64 string
    *
    * @param a_base64 the base 64 string to decode
    * @return the data
    */
  def decodeBase64(a_base64: String): Array[Byte] = Base64.decodeBase64(a_base64)
}
