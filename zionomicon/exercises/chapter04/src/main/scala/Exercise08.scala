import zio._

// Using the ZIO#foldM method, implement the following two functions,
// which make working with Either values easier,
// by shifting the unexpected case into the error channel (and reversing this shifting).
object Exercise08 {

  def right[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, A], B] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      {
        case Left(a)  => ZIO.fail(Right(a))
        case Right(b) => ZIO.succeed(b)
      }
    )

  def unright[R, E, A, B](
      zio: ZIO[R, Either[E, A], B]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      {
        case Left(e)  => ZIO.fail(e)
        case Right(a) => ZIO.succeed(Left(a))
      },
      b => ZIO.succeed(Right(b))
    )

}
