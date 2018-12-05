package com.cyclone.util.shell

/**
  * The results of running a shell command
  *
  * @author Jeremy.Stone
  */
trait CommandStreamResult {

  /**
    * The raw stream data as a list in the order captured
    */
  def streamResults: List[(ShellOutputStream, String)]

  /**
    * The command exit code
    */
  def exitCode: Int

  /**
    * Concatenates data for a specified stream
    *
    * @param outputStream the required stream
    * @return the stream as a string
    */
  def filterFor(outputStream: ShellOutputStream): String =
    streamResults
      .filter { case (cs, _) => cs == outputStream }
      .foldLeft("") { case (acc, (_, out)) => acc + out }

  /**
    * Joins the output of streams into a single string one stream after another
    *
    * @param outputStreams the streams to join
    * @return the streams as a string
    */
  def joinFor(outputStreams: Seq[ShellOutputStream]): String =
    outputStreams
      .map(cs => filterFor(cs))
      .fold("")(_ + _)

  /**
    * Joins the output of streams into a single string interleaved as they were captured
    *
    * @return the streams as a string
    */
  def interleaved: String =
    streamResults
      .map { case (_, data) => data }
      .fold("")(_ + _)
}
