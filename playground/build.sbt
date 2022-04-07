ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "playground"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "playground",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.0-RC4",
      "dev.zio" %% "zio-test" % "2.0.0-RC4" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
