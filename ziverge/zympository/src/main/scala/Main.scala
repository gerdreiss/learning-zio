import zio.*
import zio.console.*

object Main extends App:
  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    putStrLn("Welcome to your first ZIO app!").exitCode
