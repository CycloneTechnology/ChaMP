package com.cyclone.ipmi.protocol.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec._

/**
  * Holder for reading and event masks
  */
case class SensorMasks(readingMask: ReadingMask, eventMask: EventMask)

object SensorMasks {

  def decoder(eventReadingType: EventReadingType, sensorType: SensorType): Decoder[SensorMasks] =
    new Decoder[SensorMasks] {

      def decode(data: ByteString): SensorMasks = {

        val readingMask =
          eventReadingType.category match {
            case EventReadingCategory.Threshold =>
              ReadingMask.Threshold(
                (data(0).bits4To6.toUnsignedInt + (data(2).bits4To6.toUnsignedInt << 3)).toByte
                  .as[EventBits]
              )

            case EventReadingCategory.Discrete =>
              ReadingMask.DiscreteStates(
                ByteString(data(4), data(5)).as[EventBits],
                eventReadingType
              )

            case EventReadingCategory.SensorSpecific =>
              ReadingMask.SensorSpecificDiscreteStates(
                ByteString(data(4), data(5)).as[EventBits],
                sensorType
              )

            case EventReadingCategory.Digital =>
              ReadingMask.DigitalStates(
                ByteString(data(4), data(5)).as[EventBits],
                eventReadingType
              )

            case EventReadingCategory.Oem => ReadingMask.Oem
          }

        val eventMask = eventReadingType.category match {
          case EventReadingCategory.Threshold =>
            EventMask(
              assertionOffsetBits = ByteString(data(0).bits0To3, data(1)).as[EventBits],
              deassertionOffsetBits = ByteString(data(2).bits0To3, data(3)).as[EventBits]
            )

          // Other generic discrete types...
          case _: EventReadingCategory =>
            EventMask(
              assertionOffsetBits = ByteString(data(0), data(1)).as[EventBits],
              deassertionOffsetBits = ByteString(data(2), data(3)).as[EventBits]
            )
        }

        SensorMasks(readingMask, eventMask)
      }
    }
}
