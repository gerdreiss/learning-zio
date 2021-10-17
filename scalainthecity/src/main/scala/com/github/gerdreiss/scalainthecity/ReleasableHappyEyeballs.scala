package com.github.gerdreiss
package scalainthecity

import zio.*
import zio.clock.*
import zio.duration.*

object ReleasableHappyEyeballs:
  def apply[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration,
      releaseExtra: T => ZIO[R, Nothing, Unit],
    ): ZIO[R & Clock, Throwable, T] =
    for {
      successful <- Queue.bounded[T](tasks.size)
      enqueueingTasks = tasks.map(_.onExit {
        case Exit.Success(value) => successful.offer(value)
        case Exit.Failure(_)     => ZIO.unit
      })
      _       <- HappyEyeballs(enqueueingTasks, delay)
      results <- successful.takeAll
      _       <- ZIO.foreach(results.tail)(releaseExtra)
    } yield results.head
