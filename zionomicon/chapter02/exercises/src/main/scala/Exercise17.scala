import zio._

import zio.{App => ZIOApp}
import zio.console._

// Using the Console, write a little program that asks the user what their
// name is, and then prints it out to them with a greeting.
object HelloHuman extends ZIOApp {
  def run(args: List[String]) =
    (for {
      _    <- putStr("What's your name? ")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name")
    } yield ()).exitCode
}
