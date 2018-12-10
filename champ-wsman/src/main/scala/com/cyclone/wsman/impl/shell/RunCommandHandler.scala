package com.cyclone.wsman.impl.shell

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import com.cyclone.command.OperationDeadline
import com.cyclone.util.XmlUtils._
import com.cyclone.util.shell.ShellOutputStream
import com.cyclone.util.{Base64Utils, NumberUtils}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.{WSManErrorException, WSManOperationContext}
import com.cyclone.wsman.command.WSManRunShellCommand
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.model.{ManagedInstance, ManagedReference}
import com.cyclone.wsman.impl.xml.{ShellCommandXML, ShellReceiveXML}
import com.google.common.base.Charsets
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.{-\/, \/-}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.xml.{Elem, NodeSeq}

class RunCommandHandler(commandQuery: WSManRunShellCommand, deadline: OperationDeadline)(
  implicit context: WSManOperationContext
) {

  def runToSource(): Source[(List[(ShellOutputStream, String)], Option[Int]), NotUsed] = {
    val result = for {
      shell          <- eitherT(startShell())
      shellCommandId <- eitherT(runCommand(shell))
    } yield (shell, shellCommandId)

    Source
      .fromFuture(result.run)
      .flatMapConcat {
        case \/-((shell, shellCommandId)) =>
          val shellId = ShellId(shell.getSelectorValue("ShellId"))

          Source
            .unfoldAsync(Option(0)) {
              case Some(seqId) =>
                getSingleResponse(shellId, seqId, shellCommandId)
                  .map {
                    case \/-((streams, None))           => Some((Some(seqId + 1), (streams, None)))
                    case \/-((streams, Some(exitCode))) => Some((None, (streams, Some(exitCode))))
                    case -\/(e)                         => throw WSManErrorException(e)
                  }

              case None => Future.successful(None)
            }
            .alsoTo(
              Sink
                .onComplete(_ => WSManOperations.delete(shell, OperationDeadline.fromNow(1.second)))
            )

        case -\/(e) => throw WSManErrorException(e)
      }
  }

  private def startShell(): Future[WSManErrorOr[ManagedReference]] = {
    val shellInst = ManagedInstance("Shell", Namespace.MS_SHELL)
      .withProperty("InputStreams", "stdin")
      .withProperty("OutputStreams", "stdout stderr")

    WSManOperations.create(shellInst, Namespace.MS_COMMAND, None, deadline)
  }

  private def runCommand(shell: ManagedReference): Future[WSManErrorOr[ShellCommandId]] = {
    val result =
      for (response <- eitherT(
             WSManOperations
               .executeSoapRequest(
                 ShellCommandXML(commandQuery, shell.getSelectorValue("ShellId"), deadline)
               )
           )) yield ShellCommandId((response \ "Body" \ "CommandResponse" \ "CommandId").text)

    result.run
  }

  private def getSingleResponse(
    shellId: ShellId,
    seqId: Int,
    commandId: ShellCommandId
  ): Future[WSManErrorOr[(List[(ShellOutputStream, String)], Option[Int])]] = {
    val result =
      for (// TODO seqId not used (works without?)
           response <- eitherT(
             WSManOperations.executeSoapRequest(ShellReceiveXML(shellId, commandId, deadline))
           )) yield {
        val receiveResp = response \ "Body" \ "ReceiveResponse"
        val cmdStateElt = singleElement(receiveResp \ "CommandState")

        (streamDataFrom(receiveResp), exitCodeFrom(cmdStateElt))
      }

    result.run
  }

  private def streamDataFrom(responseElt: NodeSeq): List[(ShellOutputStream, String)] = {
    val streamData = ListBuffer[(ShellOutputStream, String)]()
    for (streamElt     <- elements(responseElt \ "Stream");
         commandStream <- commandStreamWithName(attributeValue(streamElt, "Name").get))
      streamData += ((commandStream, decode(streamElt)))

    streamData.toList
  }

  private def decode(streamElt: NodeSeq) =
    new String(Base64Utils.decodeBase64(streamElt.text), Charsets.UTF_8)

  private def commandStreamWithName(name: String) =
    ShellOutputStream.values.find(_.name == name.toUpperCase())

  private def exitCodeFrom(cmdStateElt: Elem): Option[Int] = {
    if (attributeValue(cmdStateElt, "State").get.endsWith("Done")) {
      cmdStateElt \ "ExitCode" match {
        case NodeSeq.Empty     => Some(0)
        case exitCode: NodeSeq => Some(NumberUtils.parseInt(exitCode.text).getOrElse(0))
      }
    } else
      None
  }
}
