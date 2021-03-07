import java.io.IOException

import zio._
import zio.console._

// Using the Console service and recursion,
// write a function that will repeatedly read input
// from the console until the specified user-defined function
// evaluates to true on the input.
object Exercise19 extends App {

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    readUntil(_.length() > 10).exitCode

  def readUntil(
      acceptInput: String => Boolean
  ): ZIO[Console, IOException, String] =
    for {
      _     <- putStr("Your input: ")
      input <- getStrLn
      _     <- if (acceptInput(input))
                 putStrLn("OK").as(input)
               else
                 putStrLn("Nope, try again") *> readUntil(acceptInput)
    } yield input

}
