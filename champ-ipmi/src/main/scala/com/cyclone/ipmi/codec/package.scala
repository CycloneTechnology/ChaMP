package com.cyclone.ipmi

import java.io.InputStream

import akka.util.ByteString
import com.google.common.io.ByteStreams

import scala.annotation.tailrec

/**
  * Support methods and implicits
  */
package object codec extends CodecSupport with DefaultCodecs {

  implicit class RichByte(val byte: Byte) extends AnyVal {

    def as[A](implicit decoder: Decoder[A]): A = decoder.decode(ByteString(byte))

    def map[A](f: Byte => A): A = f(byte)

    def toUnsignedInt: Int = byte.toInt & 0xff

    def in(range: Range): Boolean = range.contains(byte.toUnsignedInt)

    def bits1To7: Byte = ((byte.toUnsignedInt & 0xff) >> 1).toByte

    def bits2To7: Byte = ((byte.toUnsignedInt & 0xff) >> 2).toByte

    def bits3To7: Byte = ((byte.toUnsignedInt & 0xff) >> 3).toByte

    def bits4To7: Byte = ((byte.toUnsignedInt & 0xff) >> 4).toByte

    def bits6To7: Byte = ((byte.toUnsignedInt & 0xff) >> 6).toByte


    def bits0To6: Byte = ((byte.toUnsignedInt & 0x7f) >> 0).toByte

    def bits5To6: Byte = ((byte.toUnsignedInt & 0x7f) >> 5).toByte

    def bits4To6: Byte = ((byte.toUnsignedInt & 0x7f) >> 4).toByte


    def bits0To5: Byte = ((byte.toUnsignedInt & 0x3f) >> 0).toByte

    def bits4To5: Byte = ((byte.toUnsignedInt & 0x3f) >> 4).toByte

    def bits3To5: Byte = ((byte.toUnsignedInt & 0x3f) >> 3).toByte


    def bits0To4: Byte = ((byte.toUnsignedInt & 0x1f) >> 0).toByte

    def bits3To4: Byte = ((byte.toUnsignedInt & 0x1f) >> 3).toByte


    def bits0To3: Byte = ((byte.toUnsignedInt & 0x0f) >> 0).toByte

    def bits2To3: Byte = ((byte.toUnsignedInt & 0x0f) >> 2).toByte


    def bits0To2: Byte = ((byte.toUnsignedInt & 0x07) >> 0).toByte

    def bits1To2: Byte = ((byte.toUnsignedInt & 0x07) >> 1).toByte


    def bits0To1: Byte = ((byte.toUnsignedInt & 0x03) >> 0).toByte


    def bit0: Boolean = (byte.toUnsignedInt & 0x01) != 0

    def bit1: Boolean = (byte.toUnsignedInt & 0x02) != 0

    def bit2: Boolean = (byte.toUnsignedInt & 0x04) != 0

    def bit3: Boolean = (byte.toUnsignedInt & 0x08) != 0

    def bit4: Boolean = (byte.toUnsignedInt & 0x10) != 0

    def bit5: Boolean = (byte.toUnsignedInt & 0x20) != 0

    def bit6: Boolean = (byte.toUnsignedInt & 0x40) != 0

    def bit7: Boolean = (byte.toUnsignedInt & 0x80) != 0

    def set0: Byte = (byte.toUnsignedInt | 0x01).toByte

    def set1: Byte = (byte.toUnsignedInt | 0x02).toByte

    def set2: Byte = (byte.toUnsignedInt | 0x04).toByte

    def set3: Byte = (byte.toUnsignedInt | 0x08).toByte

    def set4: Byte = (byte.toUnsignedInt | 0x10).toByte

    def set5: Byte = (byte.toUnsignedInt | 0x20).toByte

    def set6: Byte = (byte.toUnsignedInt | 0x40).toByte

    def set7: Byte = (byte.toUnsignedInt | 0x80).toByte
  }

  implicit class RichOptByte(val ob: Option[Byte]) extends AnyVal {
    def as[A](implicit decoder: Decoder[A]): Option[A] =
      ob.map(b => decoder.decode(ByteString(b)))
  }

  implicit class RichInt(val int: Int) extends AnyVal {
    def in(range: Range): Boolean = range.contains(int)
  }

  implicit class RichBool(val b: Boolean) extends AnyVal {
    def toBit7: Byte = (if (b) 0x80 else 0).toByte

    def toBit6: Byte = (if (b) 0x40 else 0).toByte

    def toBit5: Byte = (if (b) 0x20 else 0).toByte

    def toBit4: Byte = (if (b) 0x10 else 0).toByte

    def toBit3: Byte = (if (b) 0x08 else 0).toByte

    def toBit2: Byte = (if (b) 0x04 else 0).toByte

    def toBit1: Byte = (if (b) 0x02 else 0).toByte

    def toBit0: Byte = (if (b) 0x01 else 0).toByte
  }

  implicit class EncoderOps[A](val a: A) extends AnyVal {
    def toBin(implicit coder: Coder[A]): ByteString = coder.encode(a)

    def toByte(implicit coder: Coder[A]): Byte = toBin.apply(0)
  }

  implicit class RichInputStream(val inputStream: InputStream) extends AnyVal {
    def read(num: Int): ByteString = {
      val bs = Array.ofDim[Byte](num)

      ByteStreams.readFully(inputStream, bs)

      ByteString(bs)
    }

    def readByteOptional: Option[Byte] = {
      val b = inputStream.read()

      if (b == -1)
        None
      else
        Some(b.toByte)
    }

    def readByte: Byte = readByteOptional.get
  }

  implicit class RichByteString(val data: ByteString) extends AnyVal {

    def as[A](implicit decoder: Decoder[A]): A = decoder.decode(data)

    def toHexString(separator: String = ":"): String = data.map("%02X" format _).mkString(separator)

    def take(range: Range): ByteString = data.drop(range.start).take(range.length)

    def splitWhenever(p: Byte => Boolean): List[ByteString] = {
      // Tail recursive version of http://stackoverflow.com/a/7293881
      @tailrec
      def loop(acc: List[ByteString], xs: ByteString): List[ByteString] =
      xs match {
        case bs if bs.isEmpty => acc
        case bs               =>
          val (ys, zs) = bs.tail.span(!p(_))
          loop((ByteString(bs.head) ++ ys) :: acc, zs)
      }

      loop(List.empty, data).reverse
    }
  }

}
