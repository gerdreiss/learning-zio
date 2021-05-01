package crawler

import crawler.web.Web
import zio._
import zio.duration._
import zio.test.Assertion._
import zio.test._
import zio.test.environment.Live

object ParWebCrawlerTest extends DefaultRunnableSpec {

  val Home: URL          = URL.make("http://zio.dev").get
  val Index: URL         = URL.make("http://zio.dev/index.html").get
  val ScaladocIndex: URL = URL.make("http://zio.dev/scaladoc/index.html").get
  val About: URL         = URL.make("http://zio.dev/about").get

  val SiteIndex =
    Map(
      Home          -> """<html><body><a href="index.html">Home</a><a href="/scaladoc/index.html">Scaladocs</a></body></html>""",
      Index         -> """<html><body><a href="index.html">Home</a><a href="/scaladoc/index.html">Scaladocs</a></body></html>""",
      ScaladocIndex -> """<html><body><a href="index.html">Home</a><a href="/about">About</a></body></html>""",
      About         -> """<html><body><a href="home.html">Home</a><a href="http://google.com">Google</a></body></html>"""
    )

  val testLayer: ZLayer[Any, Nothing, Web] =
    ZLayer.succeed { (url: URL) =>
      SiteIndex.get(url) match {
        case Some(html) =>
          ZIO.succeed(html)
        case None       =>
          ZIO.fail(new java.io.FileNotFoundException(url.toString))
      }
    }

  val testRouter: URL => Boolean =
    _.url.contains("zio.dev")

  def testProcessor(ref: Ref[Map[URL, String]]): (URL, String) => ZIO[Any, Nothing, Unit] =
    (url, html) => ref.update(_ + (url -> html))

  def spec: ZSpec[Environment, Failure]                                                   =
    suite("integration tests")(
      testM("test site") {
        for {
          ref     <- Ref.make[Map[URL, String]](Map.empty)
          _       <- ParWebCrawler.crawl(Set(Home), testRouter, testProcessor(ref))
          crawled <- ref.get
        } yield assert(crawled)(equalTo(SiteIndex))
      }.provideCustomLayer(testLayer)
    )


  override def aspects: List[TestAspectAtLeastR[Live]] =
    if (TestPlatform.isJVM) List(TestAspect.timeout(60.seconds))
    else List(TestAspect.sequential, TestAspect.timeout(60.seconds))
}
