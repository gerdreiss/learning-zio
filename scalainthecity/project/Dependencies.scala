import sbt._

object Dependencies {
  case object dev {
    case object zio {
      val zio =
        "dev.zio" %% "zio" % "1.0.12"
    }
  }

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
}
