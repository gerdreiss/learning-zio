ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.gerdreiss.zionomicon"
ThisBuild / organizationName := "chapter02-exerczses"

lazy val root = (project in file("."))
  .settings(
    name := "exercises",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.4-2",
      "dev.zio" %% "zio-test" % "1.0.4-2" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
