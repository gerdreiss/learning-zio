import zio.*
import zio.stm.*

import java.io.IOException

object Philosophers extends ZIOAppDefault:

  case object Fork
  type Fork    = Fork.type
  type ForkRef = TRef[Option[Fork]]

  final case class Seat(left: ForkRef, right: ForkRef)
  final case class Table(seats: Chunk[Seat]) extends AnyVal

  def takeForks(left: ForkRef, right: ForkRef): USTM[(Fork, Fork)] =
    (left.get <*> right.get).flatMap {
      case (Some(l), Some(r)) => left.set(None) *> right.set(None).as((l, r))
      case _                  => STM.retry
    }

  def putForks(left: ForkRef, right: ForkRef)(forks: (Fork, Fork)): USTM[Unit] =
    (left.get <*> right.get).flatMap {
      case (None, None) => left.set(Some(forks._1)) *> right.set(Some(forks._2))
      case _            => STM.retry
    }

  def setupTable(size: Int): UIO[Table] =
    val makeFork = TRef.make[Option[Fork]](Some(Fork))

    STM.atomically {
      for
        forks0 <- STM.foreach(0 to size)(_ => makeFork)
        forks   = forks0 ++ List(forks0.head)
        seats   = (forks zip forks.tail).map { case (l, r) => Seat(l, r) }
      yield Table(Chunk.fromIterable(seats))
    }

  def eat(philosopher: Int, roundtable: Table): ZIO[Console, IOException, Unit] =
    val placement = roundtable.seats(philosopher)

    val left  = placement.left
    val right = placement.right

    for
      forks <- takeForks(left, right).commit
      _     <- Console.printLine(s"Philosopher $philosopher eating...")
      _     <- putForks(left, right)(forks).commit
      _     <- Console.printLine(s"Philosopher $philosopher is done eating.")
    yield ()

  def run: ZIO[Console, IOException, Unit] =
    val count = 10

    def eaters(table: Table): Iterable[ZIO[Console, IOException, Unit]] =
      (1 to count).map(index => eat(index, table))

    for
      table <- setupTable(count)
      fiber <- ZIO.forkAll(eaters(table))
      _     <- fiber.join
      _     <- Console.printLine("All philosophers have eaten!")
    yield ()
