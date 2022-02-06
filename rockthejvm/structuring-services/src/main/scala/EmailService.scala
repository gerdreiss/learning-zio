import zio.*

object EmailService extends ZIOAppDefault:

  case class User(name: String, email: String)

  object UserEmailer:

    trait Service:
      def notify(user: User, message: String): Task[Unit]

    val live: ULayer[Service] = ZLayer.succeed {
      new:
        override def notify(user: User, message: String): Task[Unit] =
          Task(println(s"[User emailer] Sending '$message' to ${user.email}"))
    }

    def notify(user: User, message: String): ZIO[UserEmailer.Service, Throwable, Unit] =
      ZIO.environmentWithZIO(_.get.notify(user, message))

  end UserEmailer

  object UserDB:

    trait Service:
      def insert(user: User): Task[Unit]

    val live: ULayer[Service] = ZLayer.succeed {
      new:
        override def insert(user: User) = Task {
          println(s"[Database] writing user into database...")
        }
    }

    def insert(user: User): ZIO[Service, Throwable, Unit] =
      ZIO.environmentWithZIO(_.get.insert(user))

  end UserDB

  object UserSubscription:
    class Service(notifier: UserEmailer.Service, userDb: UserDB.Service):
      def subscribe(user: User): Task[User] =
        for
          _ <- notifier.notify(user, s"Welcome, ${user.name}")
          _ <- userDb.insert(user)
        yield user

    val live = ZLayer.fromServices[UserEmailer.Service, UserDB.Service, UserSubscription.Service] {
      (userEmailer, userDB) => Service(userEmailer, userDB)
    }

    def subscribe(user: User): ZIO[UserSubscription.Service, Throwable, User] =
      ZIO.environmentWithZIO(_.get.subscribe(user))

  end UserSubscription

  // horizontal composition
  val userBackend: ULayer[UserEmailer.Service with UserDB.Service] =
    UserEmailer.live ++ UserDB.live

  // vertical composition
  val userSubscriptionBackend: ULayer[UserSubscription.Service] =
    userBackend >>> UserSubscription.live

  override def run =
    val user = User("G", "g@mail.com")

    // UserEmailer
    //     .notify(user, "Welcome")
    //     .provideLayer(userBackend: ULayer[UserEmailer.Service with UserDB.Service])

    UserSubscription
      .subscribe(user)
      .provideLayer(userSubscriptionBackend)
