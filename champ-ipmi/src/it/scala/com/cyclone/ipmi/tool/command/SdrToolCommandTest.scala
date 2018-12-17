package com.cyclone.ipmi.tool.command

import com.cyclone.akka.ActorSystemShutdown
import com.cyclone.ipmi.RequiresRealIpmi
import com.cyclone.ipmi.protocol.sdr._
import org.scalatest.{Matchers, WordSpecLike}

/**
  * Tests for [[SdrTool]]
  */
class SdrToolCommandTest extends BaseToolCommandTest with WordSpecLike with Matchers with ActorSystemShutdown {

  "sdr command" must {
    "get all records when required" taggedAs RequiresRealIpmi in new Fixture {
      val records = executeCommand(SdrTool.Command(SdrFilter.All)).records

      records.exists {
        _.recordType == SensorDataRecordType.Full
      } shouldBe true
      records.exists {
        _.recordType == SensorDataRecordType.EventOnly
      } shouldBe true
    }

    "get records by id when required" taggedAs RequiresRealIpmi in new Fixture {
      val records = executeCommand(SdrTool.Command(SdrFilter.BySensorIds(SensorId("FAN 1")))).records

      records should not be empty
    }

    "get records by records type when required" taggedAs RequiresRealIpmi in new Fixture {
      val records = executeCommand(SdrTool.Command(SdrFilter.ByRecordTypes(SensorDataRecordType.Full))).records

      records.forall {
        _.recordType == SensorDataRecordType.Full
      } shouldBe true
    }

    "get records by sensor type" taggedAs RequiresRealIpmi in new Fixture {
      val records = executeCommand(SdrTool.Command(SdrFilter.BySensorType(SensorType.Temperature))).records

      records should not be empty
    }
  }
}
