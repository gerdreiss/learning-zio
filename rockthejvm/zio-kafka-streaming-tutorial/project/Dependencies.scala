import sbt._

object Dependencies {
  case object org {
    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % "3.2.10"
    }

    case object scalatestplus {
      val `scalacheck-1-15` =
        "org.scalatestplus" %% "scalacheck-1-15" % "3.2.10.0"
    }
  }

  case object dev {
    case object zio {
      val `zio-kafka` =
        "dev.zio" %% "zio-kafka" % "0.17.1"
      val `zio-json` =
        "dev.zio" %% "zio-json" % "0.2.0-M1"
    }
  }
}
