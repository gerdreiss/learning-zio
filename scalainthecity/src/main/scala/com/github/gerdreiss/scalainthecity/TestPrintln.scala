package com.github.gerdreiss
package scalainthecity

import zio.*
import zio.clock.*
import zio.duration.*

object TestPrintln extends zio.App:
  override def run(args: List[String]): URIO[Clock, ExitCode] =
    val start = System.currentTimeMillis()

    def log(msg: String): UIO[Unit] = UIO {
      val now    = System.currentTimeMillis()
      val second = (now - start) / 1000L
      println(s"after ${second}s: $msg")
    }

    def printSleepPrint(sleep: Duration, name: String): ZIO[Clock, Nothing, String] =
      log(s"START: $name") *>
        URIO.sleep(sleep) *>
        log(s"DONE: $name") *> UIO(name)

    def printSleepFail(sleep: Duration, name: String): ZIO[Clock, Throwable, String] =
      log(s"START: $name") *>
        URIO.sleep(sleep) *>
        log(s"FAIL: $name") *>
        IO.fail(new RuntimeException(s"FAIL: $name"))

    val result = HappyEyeballs(
      List(
        printSleepPrint(10.seconds, "task1"),
        printSleepFail(1.seconds, "task2"),
        printSleepPrint(3.seconds, "task3"),
        printSleepPrint(2.seconds, "task4"),
        printSleepPrint(2.seconds, "task5"),
      ),
      2.seconds,
    )

    result
      .tap(v => log(s"WON: $v"))
      .fold(_ => ExitCode.failure, _ => ExitCode.success)
      .untraced
