package crawler

import scala.util.control.NonFatal

final class URL private (private val parsed: java.net.URL) { self =>

  def url: String = parsed.toString

  def relative(page: String): Option[URL] =
    try {
      Some(new URL(new java.net.URL(parsed, page)))
    } catch {
      case NonFatal(_) => None
    }

  override def equals(that: Any): Boolean =
    that match {
      case that: URL => self.parsed == that.parsed
      case _         => false
    }

  override def hashCode: Int =
    parsed.hashCode()

  override def toString: String = url

}

object URL {
  def make(url: String): Option[URL] =
    try {
      Some(new URL(new java.net.URL(url)))
    } catch {
      case NonFatal(_) => None
    }

  def extractURLs(root: URL, html: String): Set[URL] = {
    val pattern = "href=[\"'](.*?)[\"']".r

    scala.util
      .Try({
        val matches = (for (m <- pattern.findAllMatchIn(html)) yield m.group(1)).toSet

        for {
          m   <- matches
          url <- URL.make(m) ++ root.relative(m)
        } yield url
      })
      .getOrElse(Set.empty)
  }
}
