import zio.*

/** 
 * Connection Pool
 */
case class Connection(id: String)

class ConnectionPool(connections: Ref[Vector[Connection]]):
  def obtain: Task[Connection] =
    connections
      .modify {
        case h +: t => (h, t)
        case _      => throw new IllegalStateException("No connections available")
      }
      .tap(c => ZIO.logInfo(s"Obtained connection ${c.id}"))

  def release(connection: Connection): Task[Unit] =
    connections
      .modify(cs => ((), cs :+ connection))
      .tap(_ => ZIO.logInfo(s"Released connection: ${connection.id}"))

object ConnectionPool:
  lazy val live: ZLayer[Any, Nothing, ConnectionPool] =
    ZLayer(
      Ref
        .make(Vector(Connection("Connection1"), Connection("Connection2"), Connection("Connection3")))
        .map(ConnectionPool(_))
    )

/**
 * DB
 */
class DB(connectioPool: ConnectionPool):
  private def connection: ZIO[Scope, Throwable, Connection] =
    ZIO.acquireRelease(connectioPool.obtain) { connection =>
      connectioPool
        .release(connection)
        .catchAll { error =>
          ZIO.logErrorCause("Error occurred when releasing a connection", Cause.fail(error))
        }
    }

  def transact[R, E, A](dbProgram: ZIO[Connection & R, E, A]): ZIO[R, E | Throwable, A] =
    ZIO.scoped {
      connection.flatMap { connection =>
        dbProgram.provideSomeLayer(ZLayer.succeed(connection))
      }
    }

object DB:
  lazy val live: ZLayer[ConnectionPool, Nothing, DB] =
    ZLayer.fromFunction(DB(_))

/**
 * Repository
 */
case class Car(maker: String, model: String, licensePlate: String)

class CarRepository():
  def exists(licensePlate: String): ZIO[Connection, Nothing, Boolean] =
    ZIO
      .service[Connection]
      .map(_ => /* perform the check */ licensePlate.startsWith("WN"))
      .tap(_ => ZIO.logInfo(s"Checked if car with license plate ${licensePlate} exists"))

  def insert(car: Car): ZIO[Connection, Nothing, Unit] =
    ZIO
      .service[Connection]
      .map(_ => /* perform the insert */ ())
      .tap(_ => ZIO.logInfo(s"Inserted car with license plate ${car.licensePlate}"))

object CarRepository:
  lazy val live: ZLayer[Any, Nothing, CarRepository] =
    ZLayer.succeed(CarRepository())

/**
 * Service
 */
case class LicensePlateAlreadyExists(licensePlate: String)

class CarService(carRepository: CarRepository, db: DB):
  def register(car: Car): ZIO[Any, Throwable | LicensePlateAlreadyExists, Unit] =
    db.transact {
      carRepository.exists(car.licensePlate).flatMap {
        case true  => ZIO.fail(LicensePlateAlreadyExists(car.licensePlate))
        case false => carRepository.insert(car)
      }
    }

object CarService:
  lazy val live: ZLayer[CarRepository & DB, Nothing, CarService] =
    ZLayer.fromFunction(CarService(_, _))

/**
 * API
 */
class CarApi(carService: CarService):
  def register(input: String): ZIO[Any, Nothing, String] =
    input.split(" ", 3).toList match
      case List(maker, model, licensePlate) =>
        var car = Car(maker, model, licensePlate)
        carService
          .register(car)
          .map(_ => s"Car with license plate ${car.licensePlate} registered")
          .catchAll {
            case _: LicensePlateAlreadyExists =>
              ZIO
                .logError(s"Car with license plate ${car.licensePlate} already exists")
                .map(_ => "Bad request: duplicate")
            case error                        =>
              ZIO
                .logErrorCause(s"Cannot register car $car", Cause.fail(error))
                .map(_ => "Internal server error")
          }
      case _                                =>
        ZIO.logError(s"Bad request: $input").map(_ => "Bad request")

object CarApi:
  lazy val live: ZLayer[CarService, Any, CarApi] =
    ZLayer.fromFunction(CarApi(_))
