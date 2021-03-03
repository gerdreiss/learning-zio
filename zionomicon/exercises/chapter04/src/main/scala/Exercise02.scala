import zio._

// Using the ZIO#foldCauseM operator and the Cause#defects method,
// implement the following function. This function should take the
// effect, inspect defects, and if a suitable defect is found, it should
// recover from the error with the help of the specified function, which
// generates a new success value for such a defect.
object Exercise02 {

  def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(
      f: Throwable => Option[A]
  ): ZIO[R, E, A] =
    zio.foldCauseM(
      _.defects.find(isSuitable).flatMap(f).fold(zio)(ZIO.succeed(_)),
      _ => zio
    )

  val isSuitable: Throwable => Boolean = ???

}
