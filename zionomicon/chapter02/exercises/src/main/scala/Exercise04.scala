import zio._
import zio.console.putStrLn
import java.io.PrintWriter
import java.io.File

object Exercise04 extends App {

  def printLine(line: String) = ZIO.effect(println(line))
  val readLine                = ZIO.effect(scala.io.StdIn.readLine())

  // Rewrite the following ZIO code that uses flatMap
  // into a for comprehension.
  printLine("What is your name?")
    .flatMap(_ =>
      readLine
        .flatMap(name => printLine(s"Hello, ${name}!"))
    )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (for {
      _    <- printLine("What is your name?")
      name <- readLine
      _    <- printLine(s"Hello, ${name}!")
    } yield ()).exitCode

}
