ThisBuild / scalaVersion     := "3.1.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.gerdreiss"
ThisBuild / organizationName := "zympository"

lazy val root = (project in file("."))
  .settings(
    name := "zympository",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core"                          % "3.3.16",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.3.16",
      "dev.zio"                       %% "zio"                           % "1.0.12",
      "dev.zio"                       %% "zio-json"                      % "0.2.0-M2",
      "dev.zio"                       %% "zio-test"                      % "1.0.12" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
