package com.cyclone.wsman.command

import com.cyclone.util.shell.{ShellOutputStream, CommandStreamResult}

/**
  * The results of a [[WSManRunShellCommand]]
  *
  * @author Jeremy.Stone
  */
case class WSManRunShellResult(
  streamResults: List[(ShellOutputStream, String)],
  exitCode: Int) extends WSManCommandResult with CommandStreamResult