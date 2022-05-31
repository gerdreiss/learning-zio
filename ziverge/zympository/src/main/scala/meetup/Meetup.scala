package meetup

import zio.*
import zio.json.*

final case class MeetupRequest(
    title: String,
    description: String,
    time: java.time.ZonedDateTime,
    webinarUrl: String
)

final case class MeetupResponse(
    id: String,
    link: String
)

object MeetupResponse:
  given JsonCodec[MeetupResponse] = DeriveJsonCodec.gen[MeetupResponse]

trait Meetup:
  def createEvent(eventRequest: MeetupRequest): Task[MeetupResponse]

object Meetup:
  def createEvent(request: MeetupRequest): ZIO[Meetup, Throwable, MeetupResponse] =
    ZIO.serviceWithZIO[Meetup](_.createEvent(request))
