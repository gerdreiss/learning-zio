import zio.*

/**
 * App
 */
object App extends ZIOAppDefault:

  override def run: ZIO[Any, Any, Any] =
    ZLayer
      .makeSome[Scope, CarApi](
        CarApi.live,
        CarService.live,
        CarRepository.live,
        DB.live,
        ConnectionPool.live
      )
      .build
      .map(_.get[CarApi])
      .flatMap { api =>
        api
          .register("BMW X5 XDRIVE 5.0")
          .flatMap(result => ZIO.log(result)) *>
          api
            .register("Toyota Corolla WE98765")
            .flatMap(Console.printLine(_)) *>
          api
            .register("VW Golf WN12345")
            .flatMap(Console.printLine(_)) *>
          api
            .register("Tesla")
            .flatMap(Console.printLine(_))
      }
