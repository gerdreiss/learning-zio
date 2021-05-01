package crawler

import crawler.web._
import zio._
import zio.console._

object ParWebCrawler extends App {

  val seeds: Set[URL] = URL
    .make("https://www.nytimes.com")
    .fold(Set.empty[URL])(url => Set(url)) ++ URL
    .make("https://www.n24.de")
    .fold(Set.empty[URL])(url => Set(url))

  var router: URL => Boolean = url =>
    url.url.contains("https://www.nytimes.com") || url.url.contains("https://www.n24.de")

  val processor: (URL, String) => ZIO[Console, Nothing, Unit] =
    (url, _) => console.putStrLn(url.url)

  def crawl[R, E](
      seeds: Set[URL],
      router: URL => Boolean,
      processor: (URL, String) => ZIO[R, E, Unit]
  ): ZIO[R with Web, Nothing, List[E]] =
    Ref.make[CrawlState[E]](CrawlState.empty).flatMap { crawlState =>
      Ref.make(0).flatMap { count =>
        Promise.make[Nothing, Unit].flatMap { promise =>
          ZIO.bracket(Queue.unbounded[URL])(_.shutdown) { queue =>
            val onDone: ZIO[Any, Nothing, Unit] =
              count.modify { n =>
                if (n == 1) (queue.shutdown <* promise.succeed(()), 0)
                else (ZIO.unit, n - 1)
              }.flatten

            val worker: ZIO[R with Web, Nothing, Unit] =
              queue.take.flatMap { url =>
                web
                  .getURL(url)
                  .flatMap { html =>
                    val urls = URL.extractURLs(url, html).filter(router)
                    for {
                      urls <-
                        crawlState.modify(state => (urls -- state.visited, state.visitAll(urls)))
                      _    <- processor(url, html).catchAll(e => crawlState.update(_.logError(e)))
                      _    <- queue.offerAll(urls)
                      _    <- count.update(_ + urls.size)
                    } yield ()
                  }
                  .ignore <* onDone
              }

            for {
              _     <- crawlState.update(_.visitAll(seeds))
              _     <- count.update(_ + seeds.size)
              _     <- queue.offerAll(seeds)
              _     <- ZIO.collectAll(ZIO.replicate(100)(worker.forever.fork))
              _     <- promise.await
              state <- crawlState.get
            } yield state.errors
          }
        }
      }
    }

  // testing the staff
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    for {
      fiber <- crawl(seeds, router, processor).provideCustomLayer(live).fork
      _     <- console.getStrLn.orDie
      _     <- fiber.interrupt
    } yield ExitCode.success

  //ZManaged
  //  .fromAutoCloseable(ZIO.effect(scala.io.Source.fromResource("duckduckgo.html")))
  //  .use { in =>
  //    URL
  //      .make("https://duckduckgo.com/")
  //      .map(url => URL.extractURLs(url, in.mkString))
  //      .fold(putStrLn("Nothing found")) { urls =>
  //        putStrLn(urls.mkString("\n"))
  //      }
  //  }
  //  .exitCode
}
