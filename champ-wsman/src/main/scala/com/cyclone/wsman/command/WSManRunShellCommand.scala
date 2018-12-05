package com.cyclone.wsman.command

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.{WSManErrorException, WSManOperationContext}
import WSManCommands.CommandExecutor
import com.cyclone.wsman.impl.shell._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * A WS-Management remote shell command query.
  *
  * The Remote Shell Protocol is most clearly seen in <a
  * href="see http://msdn.microsoft.com/en-us/library/cc251731.aspx">these</a>
  * examples.
  *
  * @author Jeremy.Stone
  */
case class WSManRunShellCommand(command: String, arguments: String*)
  extends WSManCommand

object WSManRunShellCommand {

  implicit object Executor extends CommandExecutor[WSManRunShellCommand, WSManRunShellResult] {
    def execute(command: WSManRunShellCommand)(implicit context: WSManOperationContext): Future[WSManErrorOr[WSManRunShellResult]] = {

      implicit val mat: Materializer = context.materializer

      val handler = new RunCommandHandler(command, context.operationDeadline)

      val raw = handler.runToSource().runWith(Sink.seq)

      raw
        .map { parts =>
          val exit = parts.reverse.headOption.flatMap(_._2).getOrElse(0)

          WSManRunShellResult(parts.flatMap(_._1).toList, exit).right
        }
        .recoverWith {
          case WSManErrorException(err) => err.left.point[Future]
        }
    }
  }

}

