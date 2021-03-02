import zio._

// Using the appropriate effect constructor, fix the following function so
// that it no longer fails with defects when executed. Make a note of how
// the inferred return type for the function changes.
object Exercise01 extends App {

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    failWithMessage("Oops!").exitCode

  def failWithMessage(string: String) =
    ZIO.fail(throw new Error(string))

}
