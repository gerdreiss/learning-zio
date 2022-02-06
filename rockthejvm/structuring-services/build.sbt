val scala3Version = "3.1.1"
val zioVersion    = "2.0.0-RC2"

lazy val root = project
  .in(file("."))
  .settings(
    name         := "structuring-services",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion
    )
  )
