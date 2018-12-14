package com.cyclone.ipmi.command.chassis
import com.cyclone.ipmi.command.chassis.GetChassisCapabilities.Capabilities

class GetChassisCapabilitiesTest extends BaseCommandTest {

  "GetChassisCapabilities" must {
    "work" in new Fixture {
      val result = executeCommand(GetChassisCapabilities.Command)

      result.capabilities shouldBe
      Capabilities(
        powerProvidesInterlock = false,
        providesDiagnosticInterrupt = false,
        providesFrontPanelLockout = false,
        chassisProvidesIntrusion = false
      )
    }
  }

}
