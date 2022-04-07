import zio.*

import java.io.IOException
import java.util.UUID
import scala.io.StdIn

object Main extends ZIOAppDefault:

  case class User(id: UUID, name: String)
  enum AuthError:
    case UserNotFound(id: UUID)
    case PasswordIncorrect(id: UUID)

  object legacy:
    def login(onSuccess: User => Unit, onFailure: AuthError => Unit): Unit = ???

  val login: IO[AuthError, User] =
    IO.async[Any, AuthError, User] { callback =>
      legacy.login(
        user => callback(IO.succeed(user)),
        err => callback(IO.fail(err))
      )
    }

  val promptForName: ZIO[Any, Throwable, Unit] =
    for
      _    <- ZIO.succeed(print("Name : "))
      name <- ZIO.attempt(StdIn.readLine()).catchNonFatalOrDie(Console.printError(_))
      _    <- ZIO.succeed(println(s"Hello, $name"))
    yield ()

  def fib(n: Long): UIO[Long] = UIO.succeed {
    if n <= 1 then UIO.succeed(n)
    else fib(n - 1).zipWith(fib(n - 2))(_ + _)
  }.flatten

  def fibNFiber(n: Long): UIO[Fiber[Nothing, Long]] =
    for fiber <- fib(n).fork
    yield fiber

  override def run: Task[Unit] =
    Console.print("N: ") *>
      Console.readLine
        .map(_.toLong)
        .flatMap { n =>
          for
            fiber <- fibNFiber(n)
            x     <- fiber.join.timeout(10.seconds)
            _     <- Console.print(f"fib($n) = $x")
          yield ()
        }
        .catchNonFatalOrDie(Console.printError(_))
