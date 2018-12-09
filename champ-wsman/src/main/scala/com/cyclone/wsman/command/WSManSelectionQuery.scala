package com.cyclone.wsman.command

import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink}
import com.cyclone.command.SelectorClause
import com.cyclone.util.concurrent.Futures
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.command.WSManCommands.CommandExecutor
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.model.{ManagedInstance, ManagedReference}
import com.cyclone.wsman.impl.xml.EnumXML
import com.cyclone.wsman.{WSManErrorException, WSManOperationContext}
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

protected trait WSManQueryDefn extends WSManCommand {
  def cimNamespace: Option[String]
}

object WSManQueryDefn {

  trait Executor[Query <: WSManQueryDefn]
      extends CommandExecutor[Query, WSManInstancesResult]
      with WSManInstancesResolver[Query] {

    def execute(
      query: Query
    )(implicit context: WSManOperationContext): Future[WSManErrorOr[WSManInstancesResult]] = {

      def resolveAll(
        instances: Seq[ManagedInstance]
      ): Future[WSManErrorOr[Seq[ManagedInstance]]] = {
        Futures.traverseSerially(instances)(
          resolveReferencesIfReqd(query, context, _, query.cimNamespace, context.operationDeadline)
        )
      }

      def createResult(instances: Seq[ManagedInstance]) =
        WSManInstancesResult(instances.map(_.external), propertyRestriction(query))

      val result = for {
        instances <- eitherT(executeInstancesQuery(query, resourceUriReference(query)))
        resolved  <- eitherT(resolveAll(instances))
      } yield createResult(resolved)

      result.run
    }

    protected def resourceUriReference(query: Query): ManagedReference

    protected def executeInstancesQuery(query: Query, ref: ManagedReference)(
      implicit context: WSManOperationContext
    ): Future[WSManErrorOr[Seq[ManagedInstance]]]
  }

}

trait WSManEnumerationQueryDefn extends WSManQueryDefn {

  def maxElementsPerEnumeration: Int
}

object WSManEnumerationQueryDefn {

  trait Executor[Query <: WSManEnumerationQueryDefn] extends WSManQueryDefn.Executor[Query] {
    protected def executeInstancesQuery(query: Query, ref: ManagedReference)(
      implicit context: WSManOperationContext
    ): Future[WSManErrorOr[Seq[ManagedInstance]]] = {

      val result = for {
        enumerator <- enumeratorFor(query, ref, ObjectAndReferenceEnumerationMode)
        items      <- eitherT(itemsFor(enumerator))
      } yield items

      result.run
    }
    private def enumeratorFor(query: Query, ref: ManagedReference, enumMode: EnumerationMode)(
      implicit context: WSManOperationContext
    ) = {

      val enumerationParameters =
        EnumerationParameters(query.maxElementsPerEnumeration, context.operationDeadline)

      for (response <- eitherT(
             WSManOperations.executeSoapRequest(
               EnumXML(
                 ref,
                 SelectorClause.forCimNamespace(query.cimNamespace),
                 instanceFilter(query),
                 enumMode,
                 enumerationParameters.deadline
               )
             )
           )) yield {
        val ctx = (response \ "Body" \ "EnumerateResponse" \ "EnumerationContext").text

        WSManEnumerator(ref, ctx, enumerationParameters, releaseOnClose = true)
      }
    }

    private def itemsFor(enumerator: WSManEnumerator)(
      implicit context: WSManOperationContext
    ) = {
      implicit val mat: Materializer = context.materializer

      val fBatches = enumerator.enumerate
        .toMat(Sink.seq)(Keep.right)
        .run()

      // FIXME should be in enumerator
      fBatches.onComplete {
        case Success(batches) =>
          val lastCtx = batches.foldLeft(Option.empty[String]) { (acc, batch) =>
            batch match {
              case Batch(_, BatchTypeNotLast(ctx)) => Some(ctx)
              case _                               => acc
            }
          }

          lastCtx.foreach(enumerator.close)

        case Failure(_) =>
      }

      fBatches
        .map { batches =>
          batches
            .foldLeft(Vector.empty[WSManEnumItem]) { (acc, batch) =>
              acc ++ batch.items
            }
            .flatMap(_.managedInstance)
            .toList
            .right
        }
        .recoverWith {
          case WSManErrorException(err) => err.left.point[Future]
        }
    }

    protected def instanceFilter(query: Query): InstanceFilter
  }

}
