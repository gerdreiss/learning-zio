package declarative

import scala.collection.mutable

final case class Coord(x: Int, y: Int)

sealed trait Parser[+A]:
  self =>

  def parse(input: String): Option[A] =
    var loop                       = true
    var remainder: String          = input
    var currentParser: Parser[Any] = self
    val stack                      = mutable.Stack[Any => Parser[Any]]()
    var result: Option[A]          = None

    def done(a: Option[Any]): Unit =
      a match
        case Some(value) =>
          if stack.isEmpty then
            loop = false
            result = Some(value.asInstanceOf[A])
          else
            val next = stack.pop()
            currentParser = next(value)
        case None        =>
          loop = false
          result = None

    while loop do
      currentParser match
        case Parser.Succeed(a) =>
          done(Some(a))

        case Parser.IntParser =>
          remainder.span(_.isDigit) match
            case ("", _)        =>
              done(None)
            case (digits, rest) =>
              remainder = rest
              done(Some(digits.toInt))

        case Parser.CharParser(ch) =>
          remainder.headOption.filter(_ == ch) match
            case Some(value) =>
              remainder = remainder.tail
              done(Some(value))
            case None        =>
              done(None)

        case Parser.StringParser(str) =>
          if remainder.startsWith(str) then
            remainder = remainder.drop(str.length)
            done(Some(str))
          else done(None)

        case Parser.FlatMap(parser, f) =>
          currentParser = parser
          stack.push(f.asInstanceOf[Any => Parser[Any]])

    end while

    result
  end parse

  def flatMap[B](f: A => Parser[B]): Parser[B] =
    Parser.FlatMap(self, f)

  def map[B](f: A => B): Parser[B] =
    flatMap(a => Parser.succeed(f(a)))

  def separatedBy(parser: Parser[Any]): Parser[List[A]] = ???

object Parser:
  val int: Parser[Int]                  = IntParser
  def string(s: String): Parser[String] = StringParser(s)
  def char(c: Char): Parser[Char]       = CharParser(c)
  def succeed[A](a: A): Parser[A]       = Succeed(a)

  case object IntParser                                              extends Parser[Int]
  final case class CharParser(char: Char)                            extends Parser[Char]
  final case class StringParser(str: String)                         extends Parser[String]
  final case class Succeed[+A](a: A)                                 extends Parser[A]
  final case class FlatMap[A, B](self: Parser[A], f: A => Parser[B]) extends Parser[B]
