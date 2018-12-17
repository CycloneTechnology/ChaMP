package com.cyclone.ipmi.command.chassis
import com.cyclone.ipmi.command.chassis.GetChassisStatus.ChassisIdentifyState.Off
import com.cyclone.ipmi.command.chassis.GetChassisStatus.PowerRestorePolicy.StaysPoweredOff
import com.cyclone.ipmi.command.chassis.GetChassisStatus.{CurrentPowerState, LastPowerEvent, MiscChassisState}

class GetChassisStatusTest extends BaseCommandTest {

  "GetChassisStatus" must {
    "work" in new Fixture {
      val result = executeCommand(GetChassisStatus.Command)

      result shouldBe GetChassisStatus.CommandResult(
        CurrentPowerState(
          StaysPoweredOff,
          controlFault = false,
          fault = false,
          interlock = false,
          overload = false,
          on = true
        ),
        LastPowerEvent(
          onByIpmi = false,
          causedByFault = false,
          causedByInterlock = false,
          causedByOverload = false,
          causedByAcFailed = false
        ),
        MiscChassisState(
          Some(Off),
          coolingFault = false,
          driveFault = false,
          frontPanelLockout = false,
          chassisIntrusion = false
        ),
        None
      )
    }
  }

}
