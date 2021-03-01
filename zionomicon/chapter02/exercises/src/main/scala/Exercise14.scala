import zio._

object Exercise14 extends App {

  // Using ZIO.effectAsync, convert the following asynchronous,
  // callbackbased function into a ZIO function:
  def getCacheValue(
      key: String,
      onSuccess: String => Unit,
      onFailure: Throwable => Unit
  ): Unit =
    println(s"getting cache value for $key...")

  def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
    ZIO.effectAsync { callback =>
      getCacheValue(
        key,
        success => callback(IO.succeed(success)),
        error => callback(IO.fail(error))
      )
    }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    getCacheValueZio("ze_key").exitCode

}
