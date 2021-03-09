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

val testFramework = new TestFramework("zio.test.sbt.ZTestFramework")

lazy val chapter02 = (project in file("chapter02"))
  .settings(
    name := "chapter02",
    libraryDependencies ++= Dependencies.zio,
    testFrameworks += testFramework
  )

lazy val chapter04 = (project in file("chapter04"))
  .settings(
    name := "chapter04",
    libraryDependencies ++= Dependencies.zio,
    testFrameworks += testFramework
  )

lazy val chapter05 = (project in file("chapter05"))
  .settings(
    name := "chapter04",
    libraryDependencies ++=
      Dependencies.zio ++
      Dependencies.doobie ++
      Dependencies.http4s ++
      Dependencies.cats,
    testFrameworks += testFramework
  )

lazy val `zionomicon-exercises` =
  project
    .in(file("."))
    .aggregate(chapter02, chapter04, chapter05)
    .settings(
      name := "zionomicon-exercises"
    )
