import zio._

object Exercise04 extends App {

  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))

  val readLine: Task[String] = ZIO.effect(scala.io.StdIn.readLine())

  // Rewrite the following ZIO code that uses flatMap
  // into a for comprehension.
  printLine("What is your name?") *>
    readLine
      .flatMap(name => printLine(s"Hello, $name!"))

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (for {
      _    <- printLine("What is your name?")
      name <- readLine
      _    <- printLine(s"Hello, ${name}!")
    } yield ()).exitCode

}
