package com.cyclone.command

import scalaz.\/

import scala.concurrent.Future

/**
  * Holder trait for channel type commands.
  */
trait Commands {

  /**
    * Context type for running commands (e.g. network connection related)
    */
  type Ctx

  /**
    * The (base) command type
    */
  type Command

  /**
    * The (base) command result type
    */
  type Result

  /**
    * The error type
    */
  type Err

  /**
    * Knows how to execute a command of a particular type
    */
  trait CommandExecutor[C <: Command, R <: Result] extends Serializable {
    def execute(command: C)(implicit context: Ctx): Future[Err \/ R]
  }
}
