import zio.*

object ZLayersPlayground extends ZIOAppDefault:

  // ZIO = "effects"
  val meaningOfLife = ZIO.succeed(42)
  val failure       = ZIO.fail("Something went terribly wrong")

  val greeting =
    for
      _    <- Console.putStrLn("Hi, what's your name")
      name <- Console.getStrLn
      _    <- Console.putStrLn(s"Hello, $name")
    yield ()

  override def run: URIO[Console, ExitCode] =
    greeting.exitCode
