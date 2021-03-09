import zio._
import zio.clock._
import zio.console._
import zio.duration._

object ZIOExample extends App {

  val child: ZIO[Clock with Console, Nothing, Unit] =
    ZIO.sleep(5.seconds) *> putStrLn("Hello from a child fiber!")

  val parent: ZIO[Clock with Console, Nothing, Unit] =
    child.fork *> ZIO.sleep(3.seconds) *> putStrLn("Hello from a parent fiber!")

  val example: ZIO[Clock with Console, Nothing, Unit] =
    for {
      fiber <- parent.fork
      _ <- ZIO.sleep(1.second)
      _ <- fiber.interrupt
      _ <- ZIO.sleep(10.seconds)
    } yield ()

  def run(args: List[String]): zio.URIO[zio.ZEnv,zio.ExitCode] =
    example.exitCode
}
