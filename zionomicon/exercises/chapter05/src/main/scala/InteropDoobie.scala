import cats.effect._
import doobie._
import doobie.hikari._
import doobie.implicits._
import zio._
import zio.blocking._
import zio.interop.catz._

object InteropDoobie {

  lazy val hikariTransactor
      : ZManaged[Blocking, Throwable, HikariTransactor[Task]] =
    for {
      blockingExecutor <- blockingExecutor.toManaged_
      runtime          <- ZIO.runtime[Any].toManaged_
      transactor       <-
        HikariTransactor
          .newHikariTransactor[Task](
            driverClassName = ???,
            url = ???,
            user = ???,
            pass = ???,
            connectEC = runtime.platform.executor.asEC,
            blocker = Blocker.liftExecutionContext(blockingExecutor.asEC)
          )
          .toManagedZIO
    } yield transactor

  val query: ConnectionIO[Int] =
    sql"select 42".query[Int].unique

  lazy val effect: ZIO[Blocking, Throwable, Int] =
    hikariTransactor.use(transactor => query.transact(transactor))

}
