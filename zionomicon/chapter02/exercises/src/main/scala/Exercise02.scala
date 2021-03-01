import zio._
import zio.console.putStrLn
import java.io.PrintWriter
import java.io.File

// Implement a ZIO version of the function writeFile
// by using the ZIO.effect constructor.
object Exercise02 extends App {

  def writeFile(file: String, text: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(file))
    try pw.write(text)
    finally pw.close
  }

  def writeFileZio(file: String, text: String): ZIO[Any, Throwable, Unit] =
    ZIO
      .effectTotal(new PrintWriter(new File(file)))
      .bracket(
        w => ZIO.effect(w.close()).ignore,
        w => ZIO.effect(w.write(text))
      )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    writeFileZio("target/test.txt", "test test test").exitCode

}
