package zoom

import zio.*

import java.time.ZonedDateTime

final case class ZoomRequest(title: String, description: String, datetime: ZonedDateTime)
final case class ZoomResponse(webinarUrl: String)

trait Zoom:
  def createWebinar(zoomRequest: ZoomRequest): Task[ZoomResponse]
