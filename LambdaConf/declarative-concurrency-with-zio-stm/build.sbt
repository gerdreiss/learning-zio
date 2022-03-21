val scala3Version = "3.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "declarative-concurrency-with-zio-stm",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq("dev.zio" %% "zio" % "2.0.0-RC3")
  )
