package programming.fp_part1
/**
  * Created by I311352 on 4/5/2017.
  */
class chap6 {

}

object chap6 extends App {
  val rng = new scala.util.Random
  (1 to 10).foreach(_ => println(rng.nextDouble()))


  def rollDie: Int = {
    val rng = new scala.util.Random
    rng.nextInt(6)
  }

  trait RNG {
    def nextInt: (Int, RNG)
    def nonNegativeInt(rng: RNG): (Int, RNG)
  }

  case class SimpleRNG(seed: Long) extends RNG {
    def nextInt: (Int, RNG) = {
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
      val nextRNG = SimpleRNG(newSeed)
      val n = (newSeed >>> 16).toInt
      (n, nextRNG)
    }

    def nonNegativeInt(rng: RNG): (Int, RNG) = {
      val (randomInt, rng2) = rng.nextInt
      if (randomInt < 0) {
        (-(randomInt+1), rng2)
      } else {
        (randomInt, rng2)
      }
    }

    def nonNegativeInt2(rng: RNG): (Int, RNG) = {
      val (i, r) = rng.nextInt
      (if (i < 0) -(i + 1) else i, r)
    }

    def double(rng: RNG): (Double, RNG) = {
      val (i, r) = nonNegativeInt(rng)
      (i / (Int.MaxValue.toDouble + 1), r)
    }

    def intDouble(rng: RNG): ((Int, Double), RNG) = {
      val (i, r1) = rng.nextInt
      val (d, r2) = double(r1)
      ((i, d), r2)
    }

    def ints2(count: Int)(rng: RNG): (List[Int], RNG) = {
      def go(count: Int, r: RNG, xs: List[Int]): (List[Int], RNG) =
        if (count <= 0)
          (xs, r)
        else {
          val (x, r2) = r.nextInt
          go(count - 1, r2, Cons(x, xs))
        }
      go(count, rng, List())
    }

    def map[A,B](s: Rand[A])(f: A => B): Rand[B] =
      rng => {
        val (a, rng2) = s(rng)
        (f(a), rng2)
      }

    def nonNegativeEven: Rand[Int] =
      map(nonNegativeInt)(i => i - i % 2)

    val _double: Rand[Double] =
      map(nonNegativeInt)(_ / (Int.MaxValue.toDouble + 1))

    def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = {
      rng => {
        val (v1, r1) = ra(rng)
        val (v2, r2) = rb(rng)
        (f(v1, v2), r2)
      }
    }

    def both[A,B](ra: Rand[A], rb: Rand[B]): Rand[(A,B)] =
      map2(ra, rb)((_, _))

    val randIntDouble: Rand[(Int, Double)] =
      both(int, double)

  }

  def unit[A](a: A): Rand[A] =
    rng => (a, rng)

  type Rand[+A] = RNG => (A, RNG)

  val int: Rand[Int] = _.nextInt

  println("random int is " + int(SimpleRNG(3)))
  val rng2 = SimpleRNG(42)
  (1 to 10).foreach(_ => println(rng2.nextInt))

  type State[S,+A] = S => (A,S)

  sealed trait Input
  case object Coin extends Input
  case object Turn extends Input
  case class Machine(locked: Boolean, candies: Int, coins: Int)

  object Candy {
    def update = (i: Input) => (s: Machine) =>
      (i, s) match {
        case (_, Machine(_, 0, _)) => s
        case (Coin, Machine(false, _, _)) => s
        case (Turn, Machine(true, _, _)) => s
        case (Coin, Machine(true, candy, coin)) =>
          Machine(false, candy, coin + 1)
        case (Turn, Machine(false, candy, coin)) =>
          Machine(true, candy - 1, coin)
      }

//    def simulateMachine(inputs: List[Input]): State[Machine, (Int, Int)] = for {
//      _ <- sequence(inputs map (modify[Machine] _ compose update))
//      s <- get
//    } yield (s.coins, s.candies)
  }

}

//object StateTrans extends App {
//  type State[S, +A] = S => (A, S)
//  case class State[S,+A](run: S => (A,S))
//}

