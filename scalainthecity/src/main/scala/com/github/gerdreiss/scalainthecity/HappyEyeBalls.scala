package com.github.gerdreiss
package learningscala3
package scalainthecity

import zio.*
import zio.clock.*

import java.time.Duration

object HappyEyeballs:
  def apply[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration,
    ): ZIO[R & Clock, Throwable, T] =
    tasks match
      case Nil => IO.fail(new java.lang.IllegalArgumentException("no tasks"))
      case task :: Nil => task
      case task :: otherTasks =>
        Queue.bounded[Unit](1).flatMap { taskedFailed =>
          val taskWithSignalOnFailed = task.onError(_ => taskedFailed.offer(()))
          val sleepOrFailed = ZIO.sleep(delay).race(taskedFailed.take)

          taskWithSignalOnFailed.race(sleepOrFailed *> apply(otherTasks, delay))
        }
