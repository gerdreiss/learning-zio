import zio._
import console._

object Main extends App {
  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    ZIO.effect(putStrLn("Hello, Zionomicon Chapter 4")).exitCode
}
