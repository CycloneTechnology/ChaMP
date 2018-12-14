package com.cyclone.ipmi

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.client.IpmiConnection

/**
  * Context passed around for IPMI operations that may consist of multiple commands
  *
  * @author Jeremy.Stone
  */
case class IpmiOperationContext(connection: IpmiConnection, timeoutContext: TimeoutContext)
