package com.cyclone.ipmi.sdr

import akka.util.ByteString
import com.cyclone.ipmi.codec.Decoder

/**
  * Contains the key and body of an BMC Channel Info Record
  */
case class BmcMessageChannelInfoSdrKeyAndBody(
                                               data: ByteString) extends SdrKeyAndBody {
  val sensorIds: Seq[SensorId] = Nil
  val sensorNumbers: Seq[SensorNumber] = Nil
  val recordType: SensorDataRecordType.McDeviceLocator.type = SensorDataRecordType.McDeviceLocator
  val optSensorType: Option[SensorType] = None
}

object BmcMessageChannelInfoSdrKeyAndBody {
  implicit val decoder: Decoder[BmcMessageChannelInfoSdrKeyAndBody] = new Decoder[BmcMessageChannelInfoSdrKeyAndBody] {
    def decode(data: ByteString): BmcMessageChannelInfoSdrKeyAndBody = {
      BmcMessageChannelInfoSdrKeyAndBody(data)
      // TODO interpret these bytes as per spec ^^^
    }
  }
}