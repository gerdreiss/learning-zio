val scala3Version   = "3.1.2"
val zioVersion      = "2.0.0-RC3"
val zhttpVersion    = "2.0.0-RC4"
val sttpVersion     = "3.5.2"
val zioMagicVersion = "0.3.12"
val zioJsonVersion  = "0.3.0-RC8"

lazy val root = project
  .in(file("."))
  .settings(
    name            := "search-hacker-news",
    version         := "0.1.0-SNAPSHOT",
    scalaVersion    := scala3Version,
    libraryDependencies ++= Seq(
      // ZIO
      "dev.zio"                       %% "zio"                    % zioVersion,
      "dev.zio"                       %% "zio-test"               % zioVersion,
      "dev.zio"                       %% "zio-test-sbt"           % zioVersion,
      "dev.zio"                       %% "zio-streams"            % zioVersion,
      "dev.zio"                       %% "zio-json"               % zioJsonVersion,
      // ZIO Magic
      ("io.github.kitlangton"         %% "zio-magic"              % zioMagicVersion).cross(CrossVersion.for3Use2_13),
      // STTP
      "com.softwaremill.sttp.client3" %% "core"                   % sttpVersion,
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % sttpVersion,
      // for nice dates
      "org.ocpsoft.prettytime"         % "prettytime"             % "5.0.3.Final"
      // Embed Li Haoyi's Ammonite repl in your test project because it's cool
      // "com.lihaoyi"                   %% "ammonite"               % "2.5.2-1-fcc95af4"
    ),
    conflictWarning := ConflictWarning.disable
  )
