import zio._
import zio.console.putStrLn
import java.io.PrintWriter
import java.io.File

// Implement a ZIO version of the function readFile
// by using the ZIO.effect constructor.
object Exercise01 extends App {

  def readFile(file: String): String = {
    val source = scala.io.Source.fromFile(file)
    try source.getLines.mkString
    finally source.close()
  }

  def readFileZio(file: String): ZIO[Any, Throwable, String] =
    ZIO
      .effect(scala.io.Source.fromFile(file))
      .bracket(
        s => ZIO.effect(s.close()).ignore,
        s => ZIO.effect(s.getLines.mkString("\n"))
      )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    readFileZio(".scalafmt.conf").flatMap(putStrLn(_)).exitCode

}
