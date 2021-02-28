import zio._

// Using ZIO.fail and ZIO.succeed, implement the following function,
// which converts a List into a ZIO effect, by looking at the head element in
// the list and ignoring the rest of the elements.
object Exercise12 {

  def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
    list match {
      case a :: _ => ZIO.succeed(a)
      case _      => ZIO.fail(None)
    }

}
