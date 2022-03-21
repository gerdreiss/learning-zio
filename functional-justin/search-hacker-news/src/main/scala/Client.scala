import sttp.client3.*
import sttp.client3.httpclient.zio.*
import sttp.model.*
import zio.*
import zio.json.JsonDecoder

type HNClient = Has[HNClient.Service]

object HNClient:
  trait Service:
    def getItem(itemId: Data.HNItemID): Task[Data.HNItem]

object Client:

  type HNIO[T] = ZIO[SttpClient & Console & Clock, Throwable, T]

  // These headers are sent with every request
  val commonHeaders = Map("Content-Type" -> "application/json")

  // Base URL for all requests
  private val baseHNURL = "https://hacker-news.firebaseio.com/v0"

  private val getTopStoriesURI  = uri"${baseHNURL}/topstories.json"
  private val getNewStoriesURI  = uri"${baseHNURL}/newstories.json"
  private val getBestStoriesURI = uri"${baseHNURL}/beststories.json"

  private val getMaxItemURI = uri"${baseHNURL}/maxitem.json"

  private def getUserURI(userId: Data.HNUserID): Uri = uri"${baseHNURL}/user/$userId.json"
  private def getItemURI(itemId: Data.HNItemID): Uri = uri"${baseHNURL}/item/$itemId.json"

  // Methods to build and execute the requests

  def createRequest[T <: Data.HNData](uri: Uri, lastModified: Option[String] = None)(using
      D: JsonDecoder[T]
  ): Request[Either[String, T], Any] =
    val headers = lastModified.map("If-Modified-Since" -> _).foldLeft(commonHeaders)(_ + _)
    basicRequest
      .get(uri)
      .headers(headers)
      .mapResponse(_.flatMap(D.decodeJson))

  def getObject[T <: Data.HNData](uri: Uri)(using D: JsonDecoder[T]): HNIO[T] =
    for
      response <- send(createRequest[T](uri))
      body     <- response.code match
                    case StatusCode.Ok          =>
                      response.body match
                        case Right(data) => ZIO.succeed(data)
                        case Left(err)   =>
                          Console.printLine(s"Request failed: $err") *>
                            ZIO.fail(new Exception(err))
                    case StatusCode.NotModified =>
                      Console.printLine(s"Not modified since ${response.header(HeaderNames.LastModified)}") *>
                        ZIO.fail(new Exception("Not Modified"))
                    case _                      =>
                      ZIO.fail(new Exception(s"Unexpected response code: ${response.code}"))
    yield body

  def getTopStories: HNIO[Data.HNItemIDList]  = getObject[Data.HNItemIDList](getTopStoriesURI)
  def getNewStories: HNIO[Data.HNItemIDList]  = getObject[Data.HNItemIDList](getNewStoriesURI)
  def getBestStories: HNIO[Data.HNItemIDList] = getObject[Data.HNItemIDList](getBestStoriesURI)

  def getItem(itemId: Data.HNItemID): HNIO[Data.HNItem] = getObject[Data.HNItem](getItemURI(itemId))
  def getUser(userId: Data.HNUserID): HNIO[Data.HNUser] = getObject[Data.HNUser](getUserURI(userId))
