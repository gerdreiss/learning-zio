// Implement the foreach function in terms of the toy model of a ZIO effect.
// The function should return an effect that sequentially runs the specified
// function on every element of the
object Exercise09 extends App {

  final case class ZIO[-R, +E, +A](run: R => Either[E, A])

  def foreach[R, E, A, B](
      in: Iterable[A]
  )(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
    ZIO(r => {
      in.map(a => f(a).run(r)).partition(_.isLeft) match {
        case (Nil, as) => Right((for (Right(a) <- as) yield a).toList)
        case (es, _)   => Left((for (Left(s) <- es) yield s).head)
      }
    })

  foreach(
    Iterable("hello", "world")
  )(s => ZIO[Any, Nothing, String](_ => Right(s.map(_.toUpper))))
    .run(())
    .map(_.mkString(", "))
    .foreach(println(_))

}
