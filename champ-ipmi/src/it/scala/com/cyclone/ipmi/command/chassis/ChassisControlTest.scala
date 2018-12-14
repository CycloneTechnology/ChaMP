package com.cyclone.ipmi.command.chassis

import com.cyclone.ipmi.PrivilegeLevel
import com.cyclone.ipmi.command.chassis.ChassisControl.Control.PowerUp
import com.cyclone.ipmi.command.ipmiMessagingSupport.SetSessionPrivilegeLevel

class ChassisControlTest extends BaseCommandTest {

  "ChassisControl" must {
    "work" in new Fixture {
      val result = ipmi.withContext(target) { implicit ctx =>
        for {
          _      <- ipmi.executeCommand(SetSessionPrivilegeLevel.Command(PrivilegeLevel.Administrator))
          result <- ipmi.executeCommand(ChassisControl.Command(PowerUp))
        } yield result
      }.futureValue

      result shouldBe ChassisControl.CommandResult
    }
  }

}
