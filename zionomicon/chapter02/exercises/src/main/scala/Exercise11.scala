import zio._

// Using ZIO.fail and ZIO.succeed, implement the following function,
// which converts an Either into a ZIO effect:
object Exercise11 {

  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
    either match {
      case Left(e)  => ZIO.fail(e)
      case Right(a) => ZIO.succeed(a)
    }

}
