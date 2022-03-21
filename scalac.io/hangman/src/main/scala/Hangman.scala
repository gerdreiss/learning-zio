import zio.*
import java.io.IOException

sealed abstract case class Name private (name: String)

object Name:
  def make(name: String): Option[Name] =
    Option.when(name.nonEmpty)(new Name(name) {})

sealed abstract case class Guess private (char: Char)

object Guess:
  def make(s: String): Option[Guess] =
    Option.when(s.length == 1 && s.head.isLetter)(new Guess(s.head.toLower) {})

sealed abstract case class Word private (word: String):
  def contains(ch: Char) = word.contains(ch)
  def length: Int        = word.length
  def toList: List[Char] = word.toList
  def toSet: Set[Char]   = word.toSet

object Word:
  def make(word: String): Option[Word] =
    Option.when(word.nonEmpty && word.forall(_.isLetter))(new Word(word.toLowerCase) {})

sealed abstract case class State private (
    name: Name,
    word: Word,
    guesses: Set[Guess] = Set()
):
  def countFailures: Int            = (guesses.map(_.char) -- word.toSet).size
  def playerLost: Boolean           = countFailures > 5
  def playerWon: Boolean            = (word.toSet -- guesses.map(_.char)).isEmpty
  def addGuess(guess: Guess): State = new State(name, word, guesses + guess) {}

object State:
  def initial(name: Name, word: Word): State = new State(name, word) {}

enum GuessResult:
  case Won, Lost, Correct, Incorrect, Unchanged

object Handman extends ZIOAppDefault:

  def prompt(message: String) =
    Console.putStrLn(message) *> Console.getStrLn

  lazy val getName: ZIO[Console, IOException, Name] =
    for
      input <- prompt("What's your name? ")
      name  <- ZIO.fromOption(Name.make(input)) <>
                 (Console.putStrLn("Invalid input. Try again...") *> getName)
    yield name

  lazy val chooseWord: ZIO[Random, Nothing, Word] =
    for
      index <- Random.nextIntBounded(Words.list.length)
      word  <- ZIO
                 .fromOption(Words.list.lift(index).flatMap(Word.make))
                 .orDieWith(_ => new Error("Boom!"))
    yield word

  def renderState(state: State) =
    val hangman = ZIO(HangmanStages.list(state.countFailures))
      .orDieWith(_ => new Error("Boom!"))

    val word = state.word.toList
      .map(c => if state.guesses.map(_.char).contains(c) then s" $c " else "   ")
      .mkString

    val line = List.fill(state.word.length)(" - ").mkString

    val guesses = s" Guesses: ${state.guesses.map(_.char).mkString(", ")}"

    hangman.flatMap { hangman =>
      Console.putStrLn(s"""
      #$hangman
      #
      #$word
      #$line
      #
      #$guesses
      #
      #""".stripMargin('#'))
    }

  lazy val getGuess: ZIO[Console, IOException, Guess] =
    for
      input <- prompt("What's your next guess? ")
      guess <- ZIO.fromOption(Guess.make(input)) <>
                 (Console.putStrLn("Invalid input. Try again...") *> getGuess)
    yield guess

  def analyzeNewGuess(oldState: State, newState: State, guess: Guess): GuessResult =
    if oldState.guesses.contains(guess) then GuessResult.Unchanged
    else if newState.playerLost then GuessResult.Lost
    else if newState.playerWon then GuessResult.Won
    else if oldState.word.contains(guess.char) then GuessResult.Correct
    else GuessResult.Incorrect

  def gameLoop(oldState: State): ZIO[Console, IOException, Unit] =
    for
      guess      <- renderState(oldState) *> getGuess
      newState    = oldState.addGuess(guess)
      guessResult = analyzeNewGuess(oldState, newState, guess)
      _          <- guessResult match
                      case GuessResult.Won       =>
                        Console.putStrLn(s"Congratulations ${newState.name.name}! You Won!") *>
                          renderState(newState)
                      case GuessResult.Lost      =>
                        Console.putStrLn(
                          s"Sorry ${newState.name.name}! You lost! Word was: ${newState.word.word}"
                        ) *>
                          renderState(newState)
                      case GuessResult.Correct   =>
                        Console.putStrLn(s"Good guess, ${newState.name.name}!") *>
                          gameLoop(newState)
                      case GuessResult.Incorrect =>
                        Console.putStrLn(s"Bad guess, ${newState.name.name}!") *>
                          gameLoop(newState)
                      case GuessResult.Unchanged =>
                        Console.putStrLn(s"${newState.name.name}, You've already tried that letter!") *>
                          gameLoop(newState)
    yield ()

  def run =
    for
      name <- Console.putStrLn("Welcome to ZIO Hangman") *> getName
      word <- chooseWord
      _    <- gameLoop(State.initial(name, word))
    yield ()
