package programming.fp;

/**
 * Created by I311352 on 3/28/2017.
 */

object chap2 extends App {

  def factorial(n: Int) = {
    def loop(n: Int): Int = {
      if (n <= 1) n else n*loop(n-1)
    }

    loop(n)
  }

  def findFirst(ss: Array[String], key: String): Int = {
    def loop(n: Int): Int =
      if (n >= ss.length) -1
      else if (ss(n) == key) n
      else loop(n+1)

    loop(0)
  }

  def findFirst[A](as: Array[A], p: A=>Boolean): Int = {
    def loop(n: Int): Int ={
      if (n >= as.length) -1
      else if (p(as(n))) n
      else loop(n+1)
    }

    loop(0)
  }

  def isSorted[A](as: Array[A], ordered: (A,A) => Boolean): Boolean={
    def loop(n: Int): Boolean = {
      if (n>= as.length) true
      else if (!ordered(as(n),as(n-1))) false
      else loop(n+1)
    }
    loop(1)
  }

  def partial1[A,B,C](a: A, f: (A,B) => C): B => C = {
    (b: B) => f(a, b)
  }

  def curry[A,B,C](f: (A, B) => C): A => (B => C) = {
    a => b => f(a,b)
  }

  def uncurry[A,B,C](f: A => B => C): (A, B) => C =
    (a, b) => f(a)(b)

  def compose[A,B,C](f: B => C, g: A => B): A => C =
    a => f(g(a))

  override def main(args: Array[String]): Unit = {
    System.out.println("Factorial 10=" + factorial(10))
    val sortedArr: Array[Int] = Array(1, 2,3, 4,6,5)
    System.out.println("FindFirst " + findFirst[Int](sortedArr, (x: Int) => x == 6 ))
    System.out.println("ordered + "+ isSorted(sortedArr, (x: Int, y: Int) => x >= y))

  }
}