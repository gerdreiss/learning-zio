import zio._
import scala.concurrent.{ExecutionContext, Future}

object Exercise16 {
  trait Query
  trait Result

  // Using ZIO.fromFuture, convert the following code to ZIO:
  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] =
    ???

  def doQueryZio(query: Query): ZIO[Any, Throwable, Result] =
    ZIO.fromFuture(implicit ec => doQuery(query))

}
