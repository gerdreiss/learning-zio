import zio.*
import sttp.client3.httpclient.zio.*
import zio.magic.*
import zio.duration.*
import java.io.IOException

object SearchTopItems2 extends ZIOAppDefault:

  def checkItemForString(search: String, id: Data.HNItemID) =
    Client.getItem(id).flatMap { item =>
      printSearchResult(search)(item)
    }

  def printSearchResult(search: String)(item: Data.HNItem): IO[IOException, Unit] =
    if item.by.toLowerCase.contains(search.toLowerCase) then
      Console.printLine(s"Found in author.\n$item")
    else if item.text.toLowerCase.contains(search.toLowerCase) then
      Console.printLine(s"Found in text.\n$item")
    else if item.title.toLowerCase.contains(search.toLowerCase) then
      Console.printLine(s"Found in title.\n$item")
    else if item.url.toLowerCase.contains(search.toLowerCase) then
      Console.printLine(s"Found in URL.\n$item")
    else ZIO.succeed(())

  val frontPageSize = 30

  def app(search: String): Client.HNIO[Unit] =
    for
      _       <- Console.printLine("Fetching front page stories")
      stories <- Client.getTopStories
      _       <- Console.printLine(
                   s"Received ${stories.itemIDs.size} stories. Search for top ${frontPageSize}"
                 )
      _       <- ZIO.when(stories.itemIDs.size > 0) {
                   for
                     topStories <- ZIO.succeed(stories.itemIDs.take(frontPageSize))
                     _          <- ZIO.foreach(topStories)(checkItemForString(search, _))
                   yield ()
                 }
    yield ()

  // fails with 'java.lang.NoSuchMethodError: 'zio.ZLayer zio.ZLayer$.fromZIOEnvironment(zio.ZIO, java.lang.Object)'
  def run = app("ukraine")
    .provide(HttpClientZioBackend.layer(), Clock.live, Console.live)
