ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.gerdreiss.zionomicon"

ThisBuild / scalacOptions ++= Seq(
  // "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  // "-Wunused:_",
  // "-Wvalue-discard",
  // "-Xfatal-warnings",
  "-Ymacro-annotations"
)

val zio = Seq(
  "dev.zio" %% "zio" % "1.0.4-2",
  "dev.zio" %% "zio-test" % "1.0.4-2" % Test
)

val testFramework = new TestFramework("zio.test.sbt.ZTestFramework")

lazy val chapter02 = (project in file("chapter02"))
  .settings(
    name := "chapter02",
    libraryDependencies ++= zio,
    testFrameworks += testFramework
  )

lazy val chapter04 = (project in file("chapter04"))
  .settings(
    name := "chapter04",
    libraryDependencies ++= zio,
    testFrameworks += testFramework
  )

lazy val chapter05 = (project in file("chapter05"))
  .settings(
    name := "chapter04",
    libraryDependencies ++= zio ++ Seq(
      "org.tpolecat" %% "doobie-core" % "0.10.0",
      "org.tpolecat" %% "doobie-hikari" % "0.10.0",
      "org.http4s" %% "http4s-core" % "0.21.20",
      "org.http4s" %% "http4s-dsl" % "0.21.20",
      "org.http4s" %% "http4s-blaze-server" % "0.21.20",
      "org.typelevel" %% "cats-core" % "2.4.2",
      "org.typelevel" %% "cats-effect" % "2.3.3",
      "dev.zio" %% "zio-interop-cats" % "2.2.0.1"
    ),
    testFrameworks += testFramework
  )

lazy val `zionomicon-exercises` =
  project
    .in(file("."))
    .aggregate(chapter02, chapter04, chapter05)
    .settings(
      name := "zionomicon-exercises"
    )
