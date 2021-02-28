import zio._

object Exercise15 {

  trait User

  // Using ZIO.effectAsync, convert the following asynchronous,
  // callbackbased function into a ZIO function:
  def saveUserRecord(
      user: User,
      onSuccess: () => Unit,
      onFailure: Throwable => Unit
  ): Unit =
    ???

  def saveUserRecordZio(user: User): ZIO[Any, Throwable, Unit] =
    ZIO.effectAsync { callback =>
      saveUserRecord(
        user,
        () => callback(ZIO.succeed(())),
        err => ZIO.fail(err)
      )
    }
}
