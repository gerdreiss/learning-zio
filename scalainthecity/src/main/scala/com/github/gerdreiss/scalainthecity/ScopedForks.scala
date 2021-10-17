package com.github.gerdreiss.scalainthecity

import zio.*
import zio.console.*
import zio.duration.*
import zio.clock.Clock

object ScopedForks extends App:
  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    ZScope.make[Exit[Nothing, Unit]].flatMap { openScope =>
      {
        for {
          _ <- putStrLn("Start")
          _ <- (ZIO.sleep(3.seconds) *> putStrLn("Child done"))
            .onExit {
              case Exit.Success(value) => putStrLn("success " + value).exitCode
              case Exit.Failure(cause) => putStrLn("failure " + cause).exitCode
            }
            .forkIn(openScope.scope)
          _ <- putStrLn("Done")
        } yield ()
      }.ensuring(openScope.close(Exit.unit))
    } *> putStrLn("Outside scope")
  }.exitCode
