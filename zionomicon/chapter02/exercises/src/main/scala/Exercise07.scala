import scala.collection.View
// Implement the collectAll function in terms of the toy model of a ZIO
// effect. The function should return an effect that sequentially collects the
// results of the specified collection of effects.
object Exercise07 extends App {

  final case class ZIO[-R, +E, +A](run: R => Either[E, A])

  def collectAll[R, E, A](
      in: Iterable[ZIO[R, E, A]]
  ): ZIO[R, E, List[A]] =
    ZIO(r => {
      in.map(_.run(r)).partition(_.isLeft) match {
        case (Nil, as) => Right((for (Right(a) <- as) yield a).toList)
        case (es, _)   => Left((for (Left(s) <- es) yield s).head)
      }
    })

  collectAll(
    Iterable(
      ZIO[Any, Nothing, String](_ => Right("hello")),
      ZIO[Any, Nothing, String](_ => Right("world"))
    )
  ).run(())
    .map(_.mkString(", "))
    .map(println(_))
}
