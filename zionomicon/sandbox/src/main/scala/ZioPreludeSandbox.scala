import zio.*
import zio.console.*
import zio.prelude.classic.*

object ZioPreludeSandbox extends App:

    case class Num(number: Int)

    given Monoid[Num] with
        def identity: Num = ???
        def combine(l: => Num, r: => Num): Num = Num(l.number + r.number)
        extension (n: Num)
            def +(m: Num) = combine(n, m)

    override def run(args: List[String]): URIO[ZEnv, ExitCode] =
        val x = Num(1)
        val y = Num(2)
        val z = x + y
        putStrLn(s"Sum of $x and $y is $z").exitCode
