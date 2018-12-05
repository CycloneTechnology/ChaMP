package com.cyclone.util.concurrent

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._

import scala.concurrent._
import scala.util.{Failure, Success, Try}

/**
  * Future-related utilities.
  *
  * @author Jeremy.Stone
  */
object Futures {

  /**
    * Runs tasks sequentially (not in parallel - but still asynchronous to the caller, on the supplied executor)
    * until one of them succeeds, returning the result in a future.
    *
    * If no task succeeds, the returned future will fail.
    *
    * @param tasks tasks to run
    * @return the future containing the first successful result.
    */
  def sequentiallyUntilSuccess[T](tasks: Seq[() => Future[T]])(implicit executor: ExecutionContext): Future[T] = {

    if (tasks.isEmpty)
      throw new IllegalArgumentException("No tasks")

    val p = Promise[T]

    //  @tailrec
    def seq(lastResult: Option[Try[T]], tsks: List[() => Future[T]])(implicit executor: ExecutionContext): Unit = {
      tsks match {
        case Nil       =>
          lastResult match {
            case Some(tr) => p.complete(tr)
            case None     =>
              // Don't recurse unless a failure was set so should never get here.
              // However in order to avoid possibility of never completing the promise,
              // set it to a value...
              p.tryFailure(new IllegalStateException)
          }
        case t :: tail =>
          val f = t()
          f.onComplete {
            case ok: Success[T] => p.complete(ok)
            case tr: Failure[T] => seq(Some(tr), tail)
          }
      }
    }

    seq(None, tasks.toList)

    p.future
  }

  /**
    * Converts the errors in a disjunction wrapped by a future into failed futures
    */
  def disjunctionToFailedFuture[A, E](in: Future[E \/ A])(toThrowable: E => Throwable)(implicit ec: ExecutionContext): Future[A] = {
    in.transform {
      case Success(\/-(a)) => Success(a)
      case Success(-\/(e)) => Failure(toThrowable(e))
      case Failure(x)      => Failure(x)
    }
  }

  /**
    * Performs a traversal of a disjunction, but serially.
    */
  def traverseSerially[A, E, B](inputs: Seq[A])(f: A => Future[E \/ B])(implicit ec: ExecutionContext): Future[E \/ Seq[B]] = {
    inputs.foldLeft(Future.successful(Vector.empty[B].right[E])) {
      case (facc, a) =>
        val result = for {
          acc <- eitherT(facc)
          b <- eitherT(f(a))
        } yield acc :+ b

        result.run
    }
  }

  /**
    * Evaluates the first successful result.
    */
  def firstSuccess[A, E, B](inputs: Seq[A])(f: A => Future[E \/ B])(implicit ec: ExecutionContext): Future[Option[E \/ B]] = {
    inputs.foldLeft(Future.successful(Option.empty[E \/ B])) {
      (acc, in) =>
        acc.flatMap {
          case Some(\/-(_)) => acc
          case _            => f(in).map(Some(_))
        }
    }
  }

  /**
    * Finds the first input that maps to a future containing true.
    *
    * This is similar to Future.find, but operates serially lazily evaluating the futures from a list of inputs.
    *
    * @param inputs inputs
    * @param f      mapping function
    * @return the first input to evaluate to true if any do.
    */
  def findSerially[A](inputs: Seq[A])(f: A => Future[Boolean])(implicit executor: ExecutionContext): Future[Option[A]] = {
    inputs.foldLeft(Future.successful(Option.empty[A])) {
      case (acc, input) =>
        acc.flatMap {
          case Some(_) => acc
          case None    => f(input).map { result => result.option(input) }
        }
    }
  }

  /**
    * Determines whether any input maps to a future containing true.
    *
    * @param inputs inputs
    * @param f      mapping function
    */
  def existsSerially[A](inputs: Seq[A])(f: A => Future[Boolean])(implicit executor: ExecutionContext): Future[Boolean] =
    findSerially(inputs)(f).map(_.isDefined)

}