import zio._

// Using the ZIO#foldM method, implement the following two functions,
// which make working with Either values easier,
// by shifting the unexpected case into the error channel
// (and reversing this shifting).
object Exercise07 {
  def left[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, B], A] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      {
        case Left(a)  => ZIO.succeed(a)
        case Right(b) => ZIO.fail(Right(b))
      }
    )

  def unleft[R, E, A, B](
      zio: ZIO[R, Either[E, B], A]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      {
        case Left(e)  => ZIO.fail(e)
        case Right(b) => ZIO.succeed(Right(b))
      },
      a => ZIO.succeed(Left(a))
    )
}
