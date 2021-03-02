import zio._
import console._
import random._

// Using the Console and Random services in ZIO, write a little program that
// asks the user to guess a randomly chosen number between 1 and 3, and
// prints out if they were correct or not.
object Exercise18 extends App {
  def run(args: List[String]): URIO[Console with Random, ExitCode] =
    (for {
      int <- nextIntBetween(1, 4)
      _   <- putStr("Guess a number: ")
      num <- getStrLn
      _   <- if (int.toString == num) putStrLn("You've guess correctly")
             else putStrLn(s"Wrong! The number was $int")
    } yield ()).exitCode
}
