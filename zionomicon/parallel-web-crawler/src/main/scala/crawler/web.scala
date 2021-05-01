package crawler

import zio._
import zio.blocking._

object web {

  type Web = Has[Web.Service]

  object Web {
    trait Service {
      def getURL(url: URL): ZIO[Any, Throwable, String]
    }
  }

  def getURL(url: URL): ZIO[Web, Throwable, String] =
    ZIO.accessM(_.get.getURL(url))

  val live: ZLayer[Blocking, Nothing, Web] =
    ZLayer.fromService { blocking => (url: URL) =>
      ZManaged
        .fromAutoCloseable(ZIO.effect(scala.io.Source.fromURL(url.url)))
        .use { source =>
          //Now all requests to get content for web pages
          // will be run on a separate blocking thread pool.
          blocking.effectBlockingIO {
            source.getLines.mkString
          }
        }
    }
}
