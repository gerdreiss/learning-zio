import Dependencies._

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.gerdreiss"
ThisBuild / organizationName := "gerdreiss"

lazy val root = (project in file("."))
  .settings(
    name := "parallel-web-crawler",
    libraryDependencies ++= Seq(
      zio, `zio-test`
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
