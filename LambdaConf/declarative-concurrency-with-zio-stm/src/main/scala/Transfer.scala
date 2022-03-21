import zio.*
import zio.stm.*

import java.io.IOException

object Transfer extends ZIOAppDefault:
  type Account = TRef[Long]
  object Account:
    def make(initial: Long): UIO[Account] = TRef.makeCommit(initial)

  def transfer(from: Account, to: Account, amount: Long): UIO[Unit] =
    STM.atomically {
      for
        currentBalance <- from.get
        _              <- STM.check(currentBalance >= amount)
        _              <- from.update(_ - amount)
        _              <- to.update(_ + amount)
      yield ()
    }

  def credit(account: Account, amount: Long): UIO[Unit] =
    account.update(_ + amount).commit

  def run: ZIO[Console & Clock, IOException, Unit] =
    for
      alice        <- Account.make(1000L)
      bob          <- Account.make(0L)
      _            <- transfer(alice, bob, 10000L) race credit(alice, 100).repeat(Schedule.fixed(10.millis))
      balanceAlice <- alice.get.commit
      balanceBob   <- bob.get.commit
      _            <- Console.printLine(s"Alice has $balanceAlice USD")
      _            <- Console.printLine(s"Bob has $balanceBob USD")
    yield ()
