val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "declarative-vs-executable-encodings",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.14"
    )
  )
