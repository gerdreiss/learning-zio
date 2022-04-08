import zio.*

object Environments extends ZIOAppDefault:
  final case class Config(server: String, port: Int)

  val configString: URIO[Config, String] =
    for
      server <- ZIO.service[Config].map(_.server)
      port   <- ZIO.service[Config].map(_.port)
    yield s"Server: $server, port: $port"

  trait DatabaseOps:
    def getTableNames: Task[List[String]]
    def getColumnNames(table: String): Task[List[String]]

  val tablesAndColumns: ZIO[DatabaseOps, Throwable, (List[String], List[String])] =
    for
      tables  <- ZIO.serviceWithZIO[DatabaseOps](_.getTableNames)
      columns <- ZIO.serviceWithZIO[DatabaseOps](_.getColumnNames("user_table"))
    yield (tables, columns)

  override def run =
    configString
      .provideService(Config("localhost", 8080))
      .debug("Config: ") *>
      tablesAndColumns
        .provideService(new DatabaseOps:
          def getTableNames: Task[List[String]] =
            Task.succeed(List("user_table", "account_table"))

          def getColumnNames(table: String): Task[List[String]] =
            Task.succeed(List("id", "name", "email"))
        )
        .debug("Tables and columns: ")
