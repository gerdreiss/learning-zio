@main def runZIO(): Unit =
  val rt = zio.Runtime.default
  val program = zio.ZIO.succeed(println("Hello World!"))
  rt.unsafeRun(program)
