package crawler

final case class CrawlState[+E](errors: List[E], visited: Set[URL]) {
  def visit(url: URL): CrawlState[E]               = copy(visited = visited + url)
  def visitAll(urls: Iterable[URL]): CrawlState[E] = copy(visited = visited ++ urls)
  def logError[E1 >: E](error: E1): CrawlState[E1] = copy(errors = error :: errors)
}

object CrawlState {
  val empty: CrawlState[Nothing] = CrawlState(List.empty, Set.empty)
}
