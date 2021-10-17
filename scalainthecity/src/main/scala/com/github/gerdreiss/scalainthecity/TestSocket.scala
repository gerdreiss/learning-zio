package com.github.gerdreiss
package scalainthecity

import zio.*
import zio.blocking.*
import zio.clock.*
import zio.console.*
import zio.duration.*

import java.net.InetAddress
import java.net.Socket

object TestSocket extends App:
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    ZIO
      .effect(InetAddress.getAllByName("debian.org").nn.toList)
      .map(_.map(addr => ZIO.effect(new Socket(addr, 443))))
      .flatMap(tasks => ReleasableHappyEyeballs(tasks, 250.microseconds, closeSocket))
      .tap(v => putStrLn(s"Connected: ${v.getInetAddress}"))
      .fold(_ => ExitCode.failure, _ => ExitCode.success)

  def closeSocket(s: Socket): ZIO[Blocking, Nothing, Unit] =
    ZIO.effect(s.close()).catchAll(_ => ZIO.unit)
