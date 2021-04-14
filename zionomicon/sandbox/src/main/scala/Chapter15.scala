import zio.console.putStrLn
import zio.{App, ExitCode, Task, UManaged, URIO, ZEnv, ZIO, ZManaged}

import java.io.File
import scala.io.Source

object Chapter15 extends App {

  lazy val names: List[String] =
    new File(".").list.toList

  lazy val file: String => UManaged[File] =
    (name: String) => ZManaged.succeed(new File(name))

  lazy val files: UManaged[List[File]] =
    ZManaged.foreach(names)(file)

  lazy val currentDirFileContent: Task[List[String]] =
    files.use { files =>
      Task
        .foreach(files) { file =>
          ZIO.ifM(ZIO.succeed(file.isFile))(
            ZIO.bracket(
              Task(Source.fromFile(file)),
              (source: Source) => ZIO.succeed(source.close()),
              (source: Source) => Task(source.getLines.toList)
            ),
            ZIO.succeed(List.empty)
          )
        }
        .map(_.flatten)
    }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    // currentDirFileContent
    //   .flatMap(lines => putStrLn(lines.mkString("\n")))
    //   .exitCode
    file(".scalafmt.conf")
      .zipPar(file("build.sbt"))
      .zipPar(file("project/build.properties"))
      .use { case ((scalafmt, buildSbt), buildProps) =>
        readLines(scalafmt)
          .zipPar(readLines(buildSbt))
          .zipPar(readLines(buildProps))
      }
      .flatMap { case ((scalafmt, buildSbt), buildProps) =>
        putStrLn(List(buildProps, buildSbt, scalafmt).mkString("\n\n====\n\n"))
      }
      .exitCode

  private def readLines(file: File) = {
    ZIO.bracket(
      Task(Source.fromFile(file)),
      (buildS: Source) => ZIO.succeed(buildS.close()),
      (buildS: Source) => Task(buildS.getLines.mkString("\n"))
    )
  }
}
