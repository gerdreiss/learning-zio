import zio._

// Using the ZIO#refineWith method,
// implement the ioException function,
// which refines the error channel
// to only include the IOException error.
object Exercise05 extends App {

  def ioException[R, A](
      zio: ZIO[R, Throwable, A]
  ): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie { case e: java.io.IOException => e }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    ioException(ZIO.fail(new java.io.IOException("Boom!"))).exitCode

}
