package meetup

import sttp.client3.*
import sttp.client3.asynchttpclient.zio.SttpClient
import zio.*
import zio.duration.*
import zio.json.*

import java.time.ZonedDateTime

case class EventPostRequest(
    announce: Boolean = false,
    name: String,
    description: String,
    time: ZonedDateTime,
    duration: Duration,   // millis
    howToFindUs: String,  // event url
    publishStatus: String // draft or published
):
  def toParams: Map[String, String] =
    Map(
      "announce"       -> announce.toString,
      "name"           -> name,
      "description"    -> description,
      "time"           -> time.toString,
      "duration"       -> duration.getSeconds.toString,
      "how_to_find_us" -> howToFindUs,
      "publish_status" -> publishStatus
    )

object EventPostRequest:
  def fromMeetupRequest(eventRequest: MeetupRequest) =
    EventPostRequest(
      name = eventRequest.title,
      description = eventRequest.description,
      time = eventRequest.time,
      duration = 90.minutes,
      howToFindUs = eventRequest.webinarUrl,
      publishStatus = "draft"
    )

final case class MeetupLive(sttp: SttpClient.Service) extends Meetup:

  val organization = "zio-meetups"
  val newEventUrl  = uri"https://api.meetup.com/$organization/events?"

  override def createEvent(request: MeetupRequest): Task[MeetupResponse] =
    for
      params   <- ZIO.succeed(EventPostRequest.fromMeetupRequest(request).toParams)
      response <- sttp.send(basicRequest.post(uri"$newEventUrl?$params"))
      payload  <- ZIO
                    .fromEither(response.body)
                    .mapError(code => new Exception(s"Failing status code $code"))
      response <- ZIO
                    .fromEither(payload.fromJson[MeetupResponse])
                    .mapError(error => new Exception(s"JSON parse error: $error"))
    yield response

end MeetupLive

object MeetupLive:
  val layer: URLayer[Has[SttpClient.Service], Has[Meetup]] =
    (MeetupLive.apply _).toLayer
