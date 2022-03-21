val scala3Version = "3.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name                             := "hangman",
    version                          := "0.1.0-SNAPSHOT",
    scalaVersion                     := scala3Version,
    libraryDependencies += "dev.zio" %% "zio" % "2.0.0-RC3"
  )
