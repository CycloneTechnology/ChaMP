package com.cyclone.wsman.command

import com.cyclone.util.shell.{CommandStreamResult, ShellOutputStream}

/**
  * The results of a [[WSManRunShellCommand]]
  *
  * @author Jeremy.Stone
  */
case class WSManRunShellResult(streamResults: List[(ShellOutputStream, String)], exitCode: Int)
    extends WSManCommandResult
    with CommandStreamResult
