import zio.NonEmptyChunk
import zio.ZIO
import zio.{App => ZIOApp}

import Exercise01._

// Using the following code as a foundation, write a ZIO application that
// prints out the contents of whatever files are passed into the program as
// command-line arguments. You should use the functions readFileZio
// and writeFileZio that you developed in these exercises, as well as
// ZIO.foreach.
object Exercise10 extends ZIOApp {

  def run(commandLineArguments: List[String]) =
    commandLineArguments match {
      case Nil => ZIO.fail(println("No filenames passed")).exitCode
      case arg :: args =>
        ZIO
          .foreach(NonEmptyChunk.fromIterable(arg, args)) { file =>
            readFileZio(file)
          }
          .map {
            _.foreach(println(_))
          }
          .exitCode
    }

}
