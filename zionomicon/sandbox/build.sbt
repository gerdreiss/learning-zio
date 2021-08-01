import Dependencies._

ThisBuild / scalaVersion := "3.0.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.gerdreiss"
ThisBuild / organizationName := "gerdreiss"

lazy val root = (project in file("."))
  .settings(
    name := "sandbox",
    libraryDependencies += zio,
    libraryDependencies += zioStreams,
    libraryDependencies += scalaTest % Test
  )
