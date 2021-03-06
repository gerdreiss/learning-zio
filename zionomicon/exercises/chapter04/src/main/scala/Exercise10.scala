import zio._

// Using the ZIO#foldCauseM method,
// implement the following function.
object Exercise10 {
  def catchAllCause[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.foldCauseM(
      handler,
      ZIO.succeed(_)
    )
}
