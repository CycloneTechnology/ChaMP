package com.cyclone.wsman

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import com.cyclone.akka.{ActorSystemComponent, MaterializerComponent}
import com.cyclone.command.TimeoutContext
import com.cyclone.util.concurrent.Futures
import com.cyclone.util.net._
import com.cyclone.util.{OperationDeadline, ResettingDeadline}
import com.cyclone.wsman.WSManError.WSManErrorOr
import com.cyclone.wsman.command.WSManCommands.CommandExecutor
import com.cyclone.wsman.command._
import com.cyclone.wsman.impl._
import com.cyclone.wsman.impl.http.{DefaultWSManConnectionFactoryComponent, DefaultWSManNetworkingComponent}
import com.cyclone.wsman.impl.model.OperationsReferenceResolverComponent
import com.cyclone.wsman.impl.subscription._
import com.cyclone.wsman.impl.subscription.pull.PullDeliveryHandler
import com.cyclone.wsman.impl.subscription.push.{
  DefaultPushDeliveryRouterComponent,
  GuavaKerberosTokenCacheComponent,
  KerberosStateHousekeeperComponent
}
import com.cyclone.wsman.subscription.{SubscriptionExecutor, SubscriptionId, WSManSubscriptionDefn}
import scalaz.EitherT._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{DurationInt, FiniteDuration}

object WSMan {

  // Depending on the uri, sometimes need to append the the CIM namespace before the
  // class name and sometimes need to use a __cimselector selector element. Leave the
  // value of the uri to the user (as per openwsman wsman client) but specify namespace for CIM operations:
  val defaultCimNamespace: Option[String] = None

  private def port(ssl: Boolean): Int =
    if (ssl) WSManTarget.defaultSslPort else WSManTarget.defaultNonSslPort

  def httpUrlFor(hostAndPort: HostAndPort, ssl: Boolean): HttpUrl =
    HttpUrl.fromParts(hostAndPort.withDefaultPort(port(ssl)), "wsman", ssl)

  def httpUrlFor(host: String, ssl: Boolean): HttpUrl =
    HttpUrl.fromParts(HostAndPort.fromParts(host, port(ssl)), "wsman", ssl)

  /**
    * Use this to create a [[WSMan]] instance.
    *
    * @return
    */
  def create(implicit system: ActorSystem): WSMan = {
    val component: DefaultWSManComponent = new DefaultWSManComponent with DefaultWSManContextFactoryComponent
    with DefaultPushDeliveryRouterComponent with KerberosStateHousekeeperComponent
    with OperationsReferenceResolverComponent with DefaultWSManConnectionFactoryComponent
    with DefaultWSManNetworkingComponent with GuavaKerberosTokenCacheComponent with ActorSystemComponent
    with MaterializerComponent with Dns4sDnsLookupComponent with DnsConfigSourceComponent
    with ConfigDnsConfigSourceComponent {
      lazy val actorSystem: ActorSystem = system
      lazy val materializer: Materializer = ActorMaterializer()
    }

    component.wsman
  }
}

/**
  * WS-Management API.
  *
  * @author Jeremy.Stone
  */
trait WSMan {

  /**
    * Executes a [[WSManCommand]]
    *
    * Errors are converted to failed futures.
    */
  def executeCommand[Command <: WSManCommand, Result <: WSManCommandResult](
    target: WSManTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[Result] = {
    val raw = executeCommandOrError(target, command)

    Futures.disjunctionToFailedFuture(raw)(WSManError.toThrowable)
  }

  /**
    * Executes a [[WSManCommand]] in a context.
    *
    * Errors are converted to failed futures.
    */
  def executeCommand[Command <: WSManCommand, Result <: WSManCommandResult](command: Command)(
    implicit executor: CommandExecutor[Command, Result],
    ctx: WSManOperationContext
  ): Future[Result] = {
    val raw = executeCommandOrError(command)

    Futures.disjunctionToFailedFuture(raw)(WSManError.toThrowable)
  }

  /**
    * Executes a [[WSManCommand]] returning the result as a disjunction
    */
  def executeCommandOrError[Command <: WSManCommand, Result <: WSManCommandResult](
    target: WSManTarget,
    command: Command
  )(
    implicit timeoutContext: TimeoutContext,
    executor: CommandExecutor[Command, Result]
  ): Future[WSManErrorOr[Result]]

  /**
    * Executes a [[WSManCommand]] in a context returning the result as a disjunction
    */
  def executeCommandOrError[Command <: WSManCommand, Result <: WSManCommandResult](
    command: Command
  )(
    implicit executor: CommandExecutor[Command, Result],
    ctx: WSManOperationContext
  ): Future[WSManErrorOr[Result]] = {
    executor.execute(command)
  }

  /**
    * Utility to perform some operation (e.q. sequence of commands) with the same context.
    */
  def withContextOrError[T](target: WSManTarget)(
    operation: WSManOperationContext => Future[WSManErrorOr[T]]
  )(implicit timeoutContext: TimeoutContext): Future[WSManErrorOr[T]]

  /**
    * Utility to perform some operation (e.q. sequence of commands) with the same context.
    */
  def withContext[T](target: WSManTarget)(operation: WSManOperationContext => Future[T])(
    implicit timeoutContext: TimeoutContext
  ): Future[T]

  /**
    * Subscribes to events
    */
  def subscribe[S <: WSManSubscriptionDefn](
    target: WSManTarget,
    subscriptionDefn: S,
    deliveryHandler: DeliveryHandler = PullDeliveryHandler()
  )(implicit executor: SubscriptionExecutor[S]): Source[SubscriptionItem, SubscriptionId]

  def testConnection(
    target: WSManTarget,
    timeout: FiniteDuration = 1.minute
  ): Future[WSManErrorOr[Unit]]
}

trait WSManComponent {
  def wsman: WSMan
}

trait DefaultWSManComponent extends WSManComponent {
  self: WSManOperationContextFactoryComponent =>

  lazy val wsman: WSMan = new WSMan {

    def testConnection(target: WSManTarget, timeout: FiniteDuration): Future[WSManErrorOr[Unit]] = {
      implicit val context: WSManOperationContext =
        wsmanOperationContextFactory.wsmanContextFor(
          target,
          OperationDeadline.reusableTimeout(timeout)
        )

      def testPossibleAvailability: Future[WSManErrorOr[Unit]] =
        WSManOperations
          .determineAvailability(context.operationDeadline)
          .map { avail =>
            if (avail.possibilyAvailable) ().right
            else WSManAvailabilityTestError(avail).left
          }

      val result = for {
        _ <- eitherT(testPossibleAvailability)
        _ <- eitherT(WSManOperations.identify(context.operationDeadline))
      } yield ()

      result.run
    }

    def executeCommandOrError[Command <: WSManCommand, Result <: WSManCommandResult](
      target: WSManTarget,
      command: Command
    )(
      implicit timeoutContext: TimeoutContext,
      executor: CommandExecutor[Command, Result]
    ): Future[WSManErrorOr[Result]] = {
      implicit val context: WSManOperationContext =
        wsmanOperationContextFactory.wsmanContextFor(target, timeoutContext.deadline)

      executor.execute(command)
    }

    private def contextForSubscription(target: WSManTarget) =
      // TODO pass in a timeout for the actual subscription/unsubscription??
      // Use reasonable value for now...
      wsmanOperationContextFactory.wsmanContextFor(target, ResettingDeadline(5.seconds))

    def subscribe[S <: WSManSubscriptionDefn](
      target: WSManTarget,
      subscriptionDefn: S,
      deliveryHandler: DeliveryHandler
    )(implicit executor: SubscriptionExecutor[S]): Source[SubscriptionItem, SubscriptionId] = {
      implicit val context: WSManOperationContext = contextForSubscription(target)

      executor.source(subscriptionDefn, deliveryHandler)
    }

    def withContextOrError[T](target: WSManTarget)(
      operation: WSManOperationContext => Future[WSManErrorOr[T]]
    )(implicit timeoutContext: TimeoutContext): Future[WSManErrorOr[T]] = {

      withContext(target)(operation)
    }

    def withContext[T](target: WSManTarget)(
      operation: WSManOperationContext => Future[T]
    )(implicit timeoutContext: TimeoutContext): Future[T] = {
      val context: WSManOperationContext =
        wsmanOperationContextFactory.wsmanContextFor(target, timeoutContext.deadline)

      operation(context)
    }

  }
}
