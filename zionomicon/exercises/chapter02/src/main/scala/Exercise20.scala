import zio._

// Using recursion, write a function that will continue evaluating the specified
// effect, until the specified user-defined function evaluates to true on the
// output of the effect.
object Exercise20 {
  def doWhile[R, E, A](
      body: ZIO[R, E, A]
  )(condition: A => Boolean): ZIO[R, E, A] =
    for {
      a <- body
      _ <- if (condition(a)) ZIO.succeed(a)
           else doWhile(body)(condition)
    } yield a
}
