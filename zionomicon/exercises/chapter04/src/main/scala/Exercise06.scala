import zio._

// Using the ZIO#refineToOrDie method, narrow the error type
// of the following effect to just NumberFormatException.
object Exercise06 extends App {

  val parseNumber: ZIO[Any, Throwable, Int] =
    ZIO.effect("foo".toInt).refineOrDie { case e: NumberFormatException =>
      e
    }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    parseNumber.exitCode
}
