package com.cyclone.wsman.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.cyclone.util.OperationDeadline
import com.cyclone.util.XmlUtils.childElements
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.impl.model.{ManagedInstance, ManagedReference}
import com.cyclone.wsman.impl.xml.{PullXML, ReleaseXML}
import com.cyclone.wsman.{WSManErrorException, WSManOperationContext}
import com.typesafe.scalalogging.LazyLogging
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.xml.{Elem, Node, NodeSeq}

/**
  * The result of the enumeration is an Observable of WSManEnumItems.
  *
  * @author Jeremy.Stone
  */
object WSManEnumItem {

  def fromElementFactory(context: WSManOperationContext): () => Elem => WSManEnumItem =
    fromElement _

  val fromElement: Elem => WSManEnumItem = { elem =>
    toEnumItem(elem)
  }

  private def toEnumItem(elem: Elem): WSManEnumItem = {
    val nodeName = elem.label

    if (isReferenceItem(elem))
      WSManEnumReference(ManagedReference(elem))
    else if (namespace(elem) == Namespace.WSMAN && nodeName == "Item")
      objectAndRef(elem)
    else
      WSManEnumInstance(ManagedInstance(elem))
  }

  private def isReferenceItem(node: Node) = namespace(node) == Namespace.ADDRESSING

  private def objectAndRef(elem: Elem): WSManEnumItem = {
    def firstRefChild(elem: Elem) =
      childElements(elem).view
        .find(isReferenceItem(_))
        .map(ManagedReference(_))

    def firstObjectChild(elem: Elem) =
      childElements(elem).view
        .find(!isReferenceItem(_))
        .map(ManagedInstance(_))

    (firstObjectChild(elem), firstRefChild(elem)) match {
      case (Some(obj), Some(ref)) => WSManEnumObjectAndReference(ref, obj)
      case _                      => WSManEnumItemEmpty
    }
  }

  private def namespace(node: Node) = {
    val ns = node.getNamespace(node.prefix)

    // check firmware bug of no Namespace
    if (ns == null && node.label == "EndpointReference") Namespace.ADDRESSING else ns
  }
}

sealed trait WSManEnumItem {
  def managedInstance: Option[ManagedInstance]
}

// For EnumerateObjectAndEpr
case class WSManEnumObjectAndReference(ref: ManagedReference, inst: ManagedInstance) extends WSManEnumItem {
  def managedInstance = Some(inst)
}

object WSManEnumItemEmpty extends WSManEnumItem {
  def managedInstance: Option[ManagedInstance] = None
}

case class WSManEnumInstance(inst: ManagedInstance) extends WSManEnumItem {
  def managedInstance: Option[ManagedInstance] = Some(inst)
}

case class WSManEnumReference(ref: ManagedReference) extends WSManEnumItem {
  def managedInstance: Option[ManagedInstance] = None
}

trait EnumerationMode {
  val name: String
}

object ObjectAndReferenceEnumerationMode extends EnumerationMode {
  val name = "EnumerateObjectAndEPR"
}

object ReferenceEnumerationMode extends EnumerationMode {
  val name = "EnumerateEPR"
}

/**
  * Determines how items are enumerated
  *
  * @param maxElements the max number of elements per response message
  */
case class EnumerationParameters(maxElements: Int, deadline: OperationDeadline)

sealed trait BatchType

object BatchTypeLast extends BatchType

case class BatchTypeNotLast(nextContext: String) extends BatchType

case class Batch(items: List[WSManEnumItem], batchType: BatchType)

private[wsman] case class WSManEnumerator(
  ref: ManagedReference,
  initialContext: String,
  parameters: EnumerationParameters,
  releaseOnClose: Boolean
)(
  implicit context: WSManOperationContext
) extends LazyLogging {

  def enumerate: Source[Batch, NotUsed] = {
    Source.unfoldAsync(Option(initialContext)) {
      case Some(enumContext) =>
        fetchDocument(enumContext)
          .map {
            case \/-(document) =>
              batchFrom(document) match {
                case batch @ Batch(_, BatchTypeNotLast(nextContext)) =>
                  logger.debug(s"Non-final enum batch fetched $batch")
                  Some((Some(nextContext), batch))

                case batch @ Batch(_, BatchTypeLast) =>
                  logger.debug(s"Final enum batch fetched $batch")
                  Some((None, batch))
              }

            case -\/(e) => throw WSManErrorException(e)
          }

      case None => Future.successful(None)
    }
  }

  private def fetchDocument(enumContext: String): Future[WSManErrorOr[Elem]] =
    WSManOperations.executeSoapRequest(PullXML(ref, enumContext, parameters))

  private def batchFrom(document: Elem): Batch = {
    val pullRespElt = (document \ "Body" \ "PullResponse").head
    val itemsElt = (pullRespElt \ "Items").head

    val items = childElements(itemsElt).map(WSManEnumItem.fromElement)

    Batch(items.toList, batchType(pullRespElt))
  }

  private def batchType(pullRespElt: Node): BatchType =
    if ((pullRespElt \ "EndOfSequence") != NodeSeq.Empty)
      BatchTypeLast
    else
      BatchTypeNotLast((pullRespElt \ "EnumerationContext").text)

  def close(enumContext: String): Unit =
    if (releaseOnClose) {
      logger.debug(s"Enum closing for context $context...")
      WSManOperations.executeSoapRequest(ReleaseXML(enumContext)).onComplete {
        case Success(_) => logger.debug(s"Enum close context $context success")
        case Failure(t) => logger.warn(s"Enum close context $context failure", t)
      }
    }
}
