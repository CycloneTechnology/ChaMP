package com.cyclone.ipmi.command.chassis
import com.cyclone.ipmi.PrivilegeLevel
import com.cyclone.ipmi.command.chassis.ChassisIdentify.IdentifyInterval
import com.cyclone.ipmi.command.ipmiMessagingSupport.SetSessionPrivilegeLevel

import scala.concurrent.duration._

class ChassisIdentifyTest extends BaseCommandTest {

  "ChassisIdentify" must {
    "work" in new Fixture {

      val result = ipmi.withContext(target) { implicit ctx =>
        for {
          _      <- ipmi.executeCommand(SetSessionPrivilegeLevel.Command(PrivilegeLevel.Administrator))
          result <- ipmi.executeCommand(ChassisIdentify.Command(IdentifyInterval.OnFor(1.second)))
        } yield result
      }.futureValue

      result shouldBe ChassisIdentify.CommandResult
    }

  }
}
