import sbt._

object Dependencies {

  val zio = Seq(
    "dev.zio" %% "zio" % "1.0.4-2",
    "dev.zio" %% "zio-interop-cats" % "2.2.0.1",
    "dev.zio" %% "zio-test" % "1.0.4-2" % Test
  )

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % "0.10.0",
    "org.tpolecat" %% "doobie-hikari" % "0.10.0"
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-core" % "0.21.20",
    "org.http4s" %% "http4s-dsl" % "0.21.20",
    "org.http4s" %% "http4s-blaze-server" % "0.21.20"
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % "2.4.2",
    "org.typelevel" %% "cats-effect" % "2.3.3"
  )

}
