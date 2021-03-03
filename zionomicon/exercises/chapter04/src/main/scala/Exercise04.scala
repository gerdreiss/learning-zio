import zio._

// Using the ZIO#foldCauseM method, which “runs” an effect to an Exit
// value, implement the following function, which will execute the specified
// effect on any failure at all:
object Exercise04 extends App {

  def onAnyFailure[R, E, A](
      zio: ZIO[R, E, A],
      handler: ZIO[R, E, Any]
  ): ZIO[R, E, A] =
    zio.foldCauseM(
      _ => handler *> zio,
      a => ZIO.succeed(a)
    )

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    onAnyFailure(
      ZIO.fail("Boom!"),
      ZIO.effect(println("the handler"))
    ).exitCode
}
