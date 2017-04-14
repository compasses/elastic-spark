package programming.puzzlers

/**
  * Created by i311352 on 09/04/2017.
  */

object tips extends App {
  val p = (1 until 100).filter( n=> n%3 ==0 ).sum
  println(p)

  lazy val fs: Stream[Int] = 0 #:: fs.scanLeft(1)(_ + _)

  val r = fs.view.takeWhile(_ <= 4000000).filter(_ % 2 == 0).sum

  assert(r == 4613732) // 1 ms


  //Find the largest prime factor of a composite number.*
  def factors(n: Long): List[Long] = (2 to math.sqrt(n).toInt)
    .find(x => n % x == 0).fold(List(n))(i => i.toLong :: factors(n / i))

  val rr = factors(600851475143L)

  println(rr)
  //assert(rr == 6857) // 1 ms
}

object ListTests1 extends App {
  val numbers: List[Int] = (1 to 10).toList
  println(s"numbers: $numbers")
}
