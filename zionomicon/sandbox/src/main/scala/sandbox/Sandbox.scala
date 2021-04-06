package sandbox

import zio.ZIO._
import zio._
import zio.console._

import java.io.File
import scala.io.Source

object Sandbox extends App {

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
          ifM(ZIO.succeed(file.isFile))(
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

  lazy val parFiles: UManaged[(File, File)] =
    file(".scalafmt.conf").zipPar(file("build.sbt"))

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    // currentDirFileContent
    //   .flatMap(lines => putStrLn(lines.mkString("\n")))
    //   .exitCode
    parFiles
      .use { case (scalafmt, build) =>
        ZIO
          .bracket(
            Task(Source.fromFile(scalafmt)),
            (scalafmtS: Source) => ZIO.succeed(scalafmtS.close()),
            (scalafmtS: Source) => Task(scalafmtS.getLines.mkString("\n"))
          )
          .zipPar(
            ZIO.bracket(
              Task(Source.fromFile(build)),
              (buildS: Source) => ZIO.succeed(buildS.close()),
              (buildS: Source) => Task(buildS.getLines.mkString("\n"))
            )
          )
      }
      .flatMap { case (scalafmt, build) =>
        putStrLn(scalafmt + "\n" + build)
      }
      .exitCode
}
