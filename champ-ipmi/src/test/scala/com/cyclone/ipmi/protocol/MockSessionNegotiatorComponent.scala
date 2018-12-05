package com.cyclone.ipmi.protocol

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError.IpmiErrorOr
import com.cyclone.ipmi._
import com.cyclone.ipmi.protocol.packet.IpmiVersion
import com.cyclone.ipmi.protocol.packet.SessionId.RemoteConsoleSessionId
import com.cyclone.util.MockeryComponent
import org.jmock.AbstractExpectations._
import org.jmock.Expectations

import scala.concurrent.Future

/**
  * Mocked [[SessionNegotiatorComponent]]
  */
class MockSessionNegotiatorComponent extends SessionNegotiatorComponent {
  self: MockeryComponent =>

  lazy val sessionNegotiator = mockery.mock(classOf[SessionNegotiator])

  def willNegotiateSession(result: Future[IpmiErrorOr[(SessionContext, IpmiVersion)]]) =
    mockery.checking(new Expectations {
      oneOf(sessionNegotiator).negotiateSession(
        `with`(aNonNull(classOf[IpmiVersionRequirement])),
        `with`(aNonNull(classOf[RemoteConsoleSessionId])),
        `with`(aNonNull(classOf[IpmiCredentials])),
        `with`(aNonNull(classOf[PrivilegeLevel])),
        `with`(aNonNull(classOf[Requester]))
      )(`with`(aNonNull(classOf[TimeoutContext])))

      will(returnValue(result))
    })
}
