ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.gerdreiss"
ThisBuild / organizationName := "zympository"

lazy val root = (project in file("."))
  .settings(
    name                                            := "zympository",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core"                          % "3.6.2",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.6.2",
      "dev.zio"                       %% "zio"                           % "1.0.14",
      "dev.zio"                       %% "zio-json"                      % "0.3.0-RC8",
      "dev.zio"                       %% "zio-test"                      % "1.0.14" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    excludeDependencies += "org.scala-lang.modules" %% "scala-collection-compat"
  )
