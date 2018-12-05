package com.cyclone.wsman.impl

import java.util.concurrent.Executor

import com.ning.http.client.ListenableFuture

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.implicitConversions
import scala.util.control.NonFatal

package object http {
  implicit def toJavaExecutor(execContext: ExecutionContext): Executor =
    (runnable: Runnable) => execContext.execute(runnable)

  implicit def ningFutureToFuture[T](
    ningFuture: ListenableFuture[T]
  )(implicit executionContext: ExecutionContext): Future[T] = {
    val p = Promise[T]

    ningFuture.addListener(() => {
      try {
        p.trySuccess(ningFuture.get)
      } catch {
        case NonFatal(e) => p.tryFailure(e)
      }
    }, executionContext)

    p.future
  }

}
