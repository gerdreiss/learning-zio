import zio._

import Exercise01._
import Exercise02._

// Using the flatMap method of ZIO effects,
// together with the readFileZio and writeFileZio functions,
// implement a ZIO version of the function copyFile.
object Exercise03 extends App {

  def copyFile(source: String, dest: String): Unit = {
    val contents = readFile(source)
    writeFile(dest, contents)
  }

  def copyFileZio(source: String, dest: String): ZIO[Any, Throwable, Unit] =
    readFileZio(source).flatMap(writeFileZio(dest, _))

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    copyFileZio(".scalafmt.conf", "target/.scalafmt.copy").exitCode

}
