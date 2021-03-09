import cats.data.Kleisli
import org.http4s._
import org.http4s.dsl.request._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze._
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._

object InteropHttp4s {

  val helloRoute: HttpRoutes[Task] =
    HttpRoutes.of[Task] { case GET -> Root / "hello" / name =>
      Response(Status.Ok).withBody(s"Hello, $name from ZIO on a server!")
    }

  val httpApp: Kleisli[Task, Request[Task], Response[Task]] =
    Router("/" -> helloRoute).orNotFound

  val server: ZManaged[Any, Throwable, Server[Task]]        =
    ZIO.runtime[Any].toManaged_.flatMap { implicit runtime =>
      BlazeServerBuilder[Task](runtime.platform.executor.asEC)
        .bindHttp(8080, "localhost")
        .withHttpApp(httpApp)
        .resource
        .toManagedZIO
    }

  val useServer: Task[Nothing] =
    server.useForever
}
