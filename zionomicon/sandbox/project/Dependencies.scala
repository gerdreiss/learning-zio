import sbt._

object Dependencies {
  lazy val scalaTest  = "org.scalatest" %% "scalatest"   % "3.2.9"
  lazy val zio        = "dev.zio"       %% "zio"         % "1.0.10"
  lazy val zioStreams = "dev.zio"       %% "zio-streams" % "1.0.10"
  lazy val zioPrelude = "dev.zio"       %% "zio-prelude" % "1.0.0-RC5"
}
