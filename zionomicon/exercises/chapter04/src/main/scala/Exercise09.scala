import zio._

// Using the ZIO#sandbox and ZIO#unsandbox methods, implement the
// following function.
object Exercise09 {
  def catchAllCause[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.sandbox
      .foldM(
        ce => handler(ce), // handle the error
        a => ZIO.succeed(a)
      )
}
