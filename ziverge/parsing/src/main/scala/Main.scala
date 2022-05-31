import zio.parser.*

import zio.*

final case class ChessCoord(row: Char, col: Int)
final case class ChessMove(from: ChessCoord, to: ChessCoord)
final case class ChessGame(moves: List[ChessMove]):
  override def toString: String = s"ChessGame {\n\t${moves.mkString("\n\t")}\n}"

val game = ChessGame(
  List(
    ChessMove(ChessCoord('A', 1), ChessCoord('A', 2)),
    ChessMove(ChessCoord('A', 2), ChessCoord('A', 3)),
    ChessMove(ChessCoord('A', 3), ChessCoord('A', 4))
  )
)

val desc = """
          | A1 -> A2
          | A2 -> A3
          | A3 -> A4
          | A4 -> B1
          """.stripMargin

val charSyntax   = Syntax.charIn(chars = ('A' to 'H')*)
val intSyntax    = Syntax.digit.transform(_.asDigit, _.toString.head)
val spacesSyntax = Syntax.whitespace.repeat.unit(Chunk(' '))
val arrowSyntax  = spacesSyntax ~ Syntax.string("->", ()) ~ spacesSyntax

val chessCoordSyntax =
  (charSyntax ~ intSyntax).transform(
    { case (row, col) => ChessCoord(row, col) },
    { case ChessCoord(row, col) => (row, col) }
  )

val chessMoveSyntax: Syntax[String, Char, Char, ChessMove] =
  (chessCoordSyntax ~ arrowSyntax ~ chessCoordSyntax)
    .transform(
      { case (from, to) => ChessMove(from, to) },
      { case ChessMove(from, to) => (from, to) }
    )

val checkGameSyntax: Syntax[String, Char, Char, ChessGame] =
  spacesSyntax ~ chessMoveSyntax
    .repeatWithSep(spacesSyntax)
    .transform(
      moves => ChessGame(moves.toList),
      game => Chunk.fromIterable(game.moves)
    ) ~ spacesSyntax

object ChessGameParser extends ZIOAppDefault:

  val coordResult = chessCoordSyntax.print(ChessCoord('A', 3))
  val coordPrint  = chessCoordSyntax.parseString("A3")

  val moveResult = chessMoveSyntax.parseString("A1 -> A2")
  val movePrint  = chessMoveSyntax.print(ChessMove(ChessCoord('A', 1), ChessCoord('A', 2)))

  val gameResult = checkGameSyntax.parseString(desc)

  override def run = Console.printLine(desc) *> Console.printLine(gameResult)
