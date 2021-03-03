import zio._

// Using the ZIO#foldCauseM operator and the Cause#prettyPrint method,
// implement an operator that takes an effect, and returns a new effect
// that logs any failures of the original effect (including errors and defects),
// without changing its failure or success value.
object Exercise03 extends App {

  def logFailures[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio.foldCauseM(
      _ => zio.tapCause(cause => ZIO.effectTotal(println(cause.prettyPrint))),
      _ => zio
    )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    logFailures(ZIO.fail("Boom!")).exitCode
}
