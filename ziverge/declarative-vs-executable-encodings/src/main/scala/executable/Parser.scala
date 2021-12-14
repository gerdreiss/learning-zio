package executable

final case class Coord(x: Int, y: Int)

trait Parser[+A]:
  self =>

  def parse(input: String): Option[(A, String)]

  def parseAll(input: String): Option[A] =
    parse(input).map(_._1)

  def flatMap[B](f: A => Parser[B]): Parser[B] =
    new:
      override def parse(input: String): Option[(B, String)] =
        self.parse(input).flatMap { case (a, rest) =>
          f(a).parse(rest)
        }

  def map[B](f: A => B): Parser[B] =
    flatMap(a => Parser.succeed(f(a)))

  def separatedBy(parser: Parser[Any]): Parser[List[A]] = ???

object Parser:
  val int: Parser[Int] =
    new:
      override def parse(input: String): Option[(Int, String)] =
        input.span(_.isDigit) match
          case ("", _) => None
          case (digits, rest) => Some((digits.toInt, rest))

  def string(s: String): Parser[String] =
    new:
      override def parse(input: String): Option[(String, String)] =
        if input.startsWith(s) then Some((s, input.drop(s.length))) else None

  def char(c: Char): Parser[Char] =
    new:
      override def parse(input: String): Option[(Char, String)] =
        input.headOption.filter(_ == c).map((_, input.tail))

  def succeed[A](a: A): Parser[A] =
    new:
      override def parse(input: String): Option[(A, String)] =
        Some((a, input))
