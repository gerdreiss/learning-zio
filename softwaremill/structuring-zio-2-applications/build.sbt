val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name                             := "structuring-zio-2-applications",
    version                          := "0.1.0-SNAPSHOT",
    scalaVersion                     := scala3Version,
    libraryDependencies += "dev.zio" %% "zio" % "2.0.0-RC6"
  )
