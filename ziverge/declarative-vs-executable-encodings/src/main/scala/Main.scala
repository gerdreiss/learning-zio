import declarative.{ Coord, Parser }

@main def main(): Unit =
  val coord = for
    _ <- Parser.char('(')
    x <- Parser.int
    _ <- Parser.string(", ")
    y <- Parser.int
    _ <- Parser.char(')')
  yield Coord(x, y)
  coord.parse("(12, 30)").foreach(println)
