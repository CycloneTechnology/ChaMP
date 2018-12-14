package com.cyclone.ipmi.tool.command

import com.cyclone.ipmi.protocol.sdr._

sealed trait SdrFilter {
  val predicate: SdrKeyAndBody => Boolean
}

object SdrFilter {

  case class BySensorIds(sensorIds: SensorId*) extends SdrFilter {

    val predicate: SdrKeyAndBody => Boolean = { record: SdrKeyAndBody =>
      sensorIds.exists(record.sensorIds.contains)
    }
  }

  case class BySensorNumbers(sensorNumbers: SensorNumber*) extends SdrFilter {

    val predicate: SdrKeyAndBody => Boolean = { record: SdrKeyAndBody =>
      sensorNumbers.exists(record.sensorNumbers.contains)
    }
  }

  case class BySensorType(sensorType: SensorType) extends SdrFilter {

    val predicate: SdrKeyAndBody => Boolean = { record: SdrKeyAndBody =>
      record.optSensorType.contains(sensorType)
    }
  }

  case class ByRecordTypes(recordTypes: SensorDataRecordType*) extends SdrFilter {

    val predicate: SdrKeyAndBody => Boolean = { record: SdrKeyAndBody =>
      recordTypes.contains(record.recordType)
    }
  }

  case object All extends SdrFilter {

    val predicate: SdrKeyAndBody => Boolean = { _: SdrKeyAndBody =>
      true
    }
  }

}
