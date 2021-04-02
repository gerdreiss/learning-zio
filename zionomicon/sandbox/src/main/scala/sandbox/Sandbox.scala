package sandbox

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

import scala.io.Source

import zio._

import ZIO._
import console._
import collection.JavaConverters._

object Sandbox extends App {

  lazy val names = new File(".").list.toList
  lazy val file  = (name: String) => ZManaged.succeed(new File(name))
  lazy val files = ZManaged.foreach(names)(file)

  lazy val currentDirFileContent: Task[List[String]] =
    files.use { files =>
      Task
        .foreach(files) { file =>
          ifM(ZIO.succeed(file.isFile()))(
            Task(Source.fromFile(file).getLines.toList),
            ZIO.succeed(List.empty)
          )
        }
        .map(_.flatten)
    }

  lazy val parFiles = file(".scalafmt.conf").zipPar(file("build.sbt"))

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    // currentDirFileContent
    //   .flatMap(lines => putStrLn(lines.mkString("\n")))
    //   .exitCode
    parFiles
      .use { case (scalafmt, build) =>
        ZIO(
          (
            Source.fromFile(scalafmt).getLines.mkString("\n"),
            Source.fromFile(build).getLines.mkString("\n")
          )
        )
      }
      .flatMap { case (scalafmt, build) =>
        putStrLn(scalafmt + "\n" + build)
      }
      .exitCode
}
