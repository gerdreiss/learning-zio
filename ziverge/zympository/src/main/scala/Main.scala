import zio.*

object Main extends zio.ZIOAppDefault:
  override def run: ZIO[Environment & (ZIOAppArgs & Scope), Any, Any] =
    Console.printLine("Welcome to your first ZIO app!").exitCode
