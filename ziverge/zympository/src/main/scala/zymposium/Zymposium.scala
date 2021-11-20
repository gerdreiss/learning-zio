package zymposium

import meetup.*
import zio.*
import zoom.*

import java.time.ZonedDateTime

final case class ZymposiumRequest(title: String, description: String, datetime: ZonedDateTime)
final case class ZymposiumResponse(webinarUrl: String, meetupUrl: String)

trait Zymposium:
  def createZymposium(zymposiumRequest: ZymposiumRequest): Task[ZymposiumResponse]

object Zymposium:
  def createZymposium(
      eventRequest: ZymposiumRequest
  ): ZIO[Has[Zymposium], Throwable, ZymposiumResponse] =
    ZIO.serviceWith(_.createZymposium(eventRequest))

final case class ZymposiumLive(meetup: Meetup, zoom: Zoom) extends Zymposium:
  override def createZymposium(zymposiumRequest: ZymposiumRequest): Task[ZymposiumResponse] =
    for
      zoomRequest    <- ZIO.succeed(
                          ZoomRequest(
                            zymposiumRequest.title,
                            zymposiumRequest.description,
                            zymposiumRequest.datetime
                          )
                        )
      zoomResponse   <- zoom.createWebinar(zoomRequest)
      meetupRequest  <- ZIO.succeed(
                          MeetupRequest(
                            zymposiumRequest.title,
                            zymposiumRequest.description,
                            zymposiumRequest.datetime,
                            zoomResponse.webinarUrl
                          )
                        )
      meetupResponse <- meetup.createEvent(meetupRequest)
    yield ZymposiumResponse(zoomResponse.webinarUrl, meetupResponse.link)

object ZymposiumLive:
  val layer: URLayer[Has[Meetup] with Has[Zoom], Has[Zymposium]] =
    (ZymposiumLive.apply _).toLayer
