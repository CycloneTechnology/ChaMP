package com.cyclone.wsman.impl

import com.cyclone.command.SelectorClause
import com.cyclone.util.OperationDeadline
import com.cyclone.util.XmlUtils.{singleChildOfSingleElement, singleElement}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.WSManOperationContext
import com.cyclone.wsman.impl.model.{IdentifyResponse, ManagedInstance, ManagedReference, ReferenceResolver}
import com.cyclone.wsman.impl.xml._
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.xml.Elem

/**
  * Low level WSMan operations
  */
private[wsman] object WSManOperations extends ReferenceResolver {

  def get(ref: ManagedReference, deadline: OperationDeadline)(
    implicit context: WSManOperationContext
  ): Future[WSManErrorOr[ManagedInstance]] = {
    val result = for {
      response <- eitherT(
        context.connection.executeSoapRequest(GetXML(ref, ref.selectorClause, deadline))
      )
    } yield ManagedInstance(singleChildOfSingleElement(response \ "Body"))

    result.run
  }

  /**
    * Executes a soap request against the device
    */
  def executeSoapRequest(
    requestXML: RequestXML
  )(implicit context: WSManOperationContext): Future[WSManErrorOr[Elem]] =
    context.connection.executeSoapRequest(requestXML)

  /**
    * Determines whether the WSMan service might be available for
    * configured context (without regard to credentials).
    *
    * A positive result indicates possible availability (but maybe not with these credentials),
    * a negative result indicates (at least at the time this check is performed) no availability with
    * these or other credentials.
    */
  def determineAvailability(
    deadline: OperationDeadline
  )(implicit context: WSManOperationContext): Future[WSManAvailability] =
    context.connection.determineAvailability(deadline.timeRemaining)

  /**
    * Executes an WS-Management identify.
    */
  def identify(
    deadline: OperationDeadline
  )(implicit context: WSManOperationContext): Future[WSManErrorOr[IdentifyResponse]] = {
    val result = for {
      response <- eitherT(context.connection.executeSoapRequest(IdentifyXML(deadline)))
    } yield IdentifyResponse(singleElement(response \ "Body" \ "IdentifyResponse"))

    result.run
  }

  /**
    * Creates the specified instance on the device.
    * Uses the resourceURI from the instance.
    */
  def create(inst: ManagedInstance, cimNamespace: Option[String], deadline: OperationDeadline)(
    implicit context: WSManOperationContext
  ): Future[WSManErrorOr[ManagedReference]] =
    create(inst, inst.getResourceURI, cimNamespace, deadline)

  /**
    * Creates the specified instance on the device.
    */
  def create(
    inst: ManagedInstance,
    resourceURI: String,
    cimNamespace: Option[String],
    deadline: OperationDeadline
  )(implicit context: WSManOperationContext): Future[WSManErrorOr[ManagedReference]] = {
    val result = for {
      response <- eitherT(
        context.connection.executeSoapRequest(
          CreateXML(inst, resourceURI, SelectorClause.forCimNamespace(cimNamespace), deadline)
        )
      )
    } yield ManagedReference(singleChildOfSingleElement(response \ "Body"))

    result.run
  }

  /**
    * Deletes an object based on its reference
    */
  def delete(ref: ManagedReference, deadline: OperationDeadline)(
    implicit context: WSManOperationContext
  ): Future[WSManErrorOr[Unit]] = {
    val result = for {
      _ <- eitherT(
        context.connection
          .executeSoapRequest(DeleteXML(ref, ref.selectorClause, deadline))
      )
    } yield ()

    result.run
  }
}
