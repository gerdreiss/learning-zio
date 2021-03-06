import zio.ExitCode
import zio.URIO
import zio.console._
import zio.{App => ZIOApp}

// Using the Console, write a little program that asks the user what their
// name is, and then prints it out to them with a greeting.
object Exercise17 extends ZIOApp {
  def run(args: List[String]): URIO[Console, ExitCode] =
    (for {
      _    <- putStr("What's your name? ")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name")
    } yield ()).exitCode
}
