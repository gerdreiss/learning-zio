import cats.effect._
import cats.syntax.all._
import zio._
import zio.interop.catz._

import scala.io.StdIn

object InteropCats {

  def catsGreet[F[_]: Sync]: F[Unit] =
    for {
      name <- Sync[F].delay(StdIn.readLine("What's your name?"))
      _    <- Sync[F].delay(println(s"Hello, $name!"))
    } yield ()

  val zioGreetPure: UIO[Unit] =
    for {
      name <- UIO.effectTotal(StdIn.readLine("What's your name?"))
      _    <- UIO.effectTotal(println(s"Hello, $name!"))
    } yield ()

  val zioGreetCatz: Task[Unit] =
    catsGreet[Task]
}
