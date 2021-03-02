import zio._

object Exercise05 extends App {

  val random = ZIO.effect(scala.util.Random.nextInt(3) + 1)

  def printLine(line: String) = ZIO.effect(println(line))

  val readLine = ZIO.effect(scala.io.StdIn.readLine())

  // Rewrite the following ZIO code that uses flatMap
  // into a for comprehension.
  random.flatMap(int =>
    printLine("Guess a number from 1 to 3:") *>
      readLine
        .flatMap(num =>
          if (num == int.toString) printLine("You guessed right!")
          else printLine(s"You guessed wrong, the number was ${int}!")
        )
  )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (for {
      int <- random
      _ <- printLine("Guess a number from 1 to 3:")
      num <- readLine
      _ <- if (num == int.toString)
        printLine("You guessed right!")
      else
        printLine(s"You guessed wrong, the number was ${int}!")
    } yield ()).exitCode

}
