ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.gerdreiss.zionomicon"

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Wunused:_",
  "-Wvalue-discard",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)

val zio = Seq(
  "dev.zio" %% "zio" % "1.0.4-2",
  "dev.zio" %% "zio-test" % "1.0.4-2" % Test
)

lazy val chapter02 = (project in file("chapter02"))
  .settings(
    name := "chapter02",
    libraryDependencies ++= zio,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val chapter04 = (project in file("chapter04"))
  .settings(
    name := "chapter04",
    libraryDependencies ++= zio,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val `zionomicon-exercises` =
  project
    .in(file("."))
    .aggregate(chapter02, chapter04)
    .settings(
      name := "zionomicon-exercises"
    )
