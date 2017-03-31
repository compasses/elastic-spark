package programming.fp

/**
  * Created by I311352 on 3/31/2017.
  */
class chap5 {

}

sealed trait Stream[+A] {
  def empty[A]: Stream[A] = Empty

  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    SCons(() => head, () => tail)
  }

  def headOption: Option[A] = this match {
    case Empty => None
    case SCons(h, t) => Some(h())
  }

  def toList: List[A] = this match {
    case Empty => Nil
    case SCons(h, t) => Cons(h(), t() toList)
  }

  def toListA: List[A] = {
    @annotation.tailrec
    def go(s: Stream[A], acc: List[A]): List[A] = s match {
      case SCons(h,t) => go(t(), Cons(h(), acc))
      case _ => acc
    }

    go(this, List())
  }

  def toListFast: List[A] = {
    val buf = new collection.mutable.ListBuffer[A]
    @annotation.tailrec
    def go(s: Stream[A]): List[A] = s match {
      case SCons(h,t) =>
        buf += h()
        go(t())
      case _ => Nil
    }
    go(this)
  }

  def take(n : Int) : Stream[A] = this  match {
    case SCons(h, t) if n > 1 => cons(h(), t().take(n-1))
    case SCons(h, _) if n == 1 => cons(h(), empty)
    case _ => empty
  }

  def drop(n: Int) : Stream[A] = this match {
    case SCons(_, t) if n > 0 => t().drop(n - 1)
    case _ => this
    //    case SCons(h, t) if n == 0 => cons(h(), t())
    //    case SCons(h, t) if n >= 0 => this drop(n-1)
    //    case _ => empty
  }

  def takeWhile(p: A => Boolean): Stream[A] = this match {
    case SCons(h, t) if (p(h())) => cons(h(), t().takeWhile(p))
    case _ => empty
  }

  def exists(p: A => Boolean): Boolean = this match {
    case SCons(h, t) => p(h()) || t().exists(p)
    case _ => false
  }

  def foldRight[B](z: => B)(f: (A, => B) => B): B =
    this match {
      case SCons(h, t) => f(h(), t().foldRight(z)(f))
      case _ => z
    }

  def exists_1(p: A => Boolean): Boolean =
    foldRight(false)((a, b) => p(a) || b)

  def forAll(p: A => Boolean): Boolean = this match {
    case SCons(h, t) =>  if (!p(h())) false else t().forAll(p)
    case empty => true
  }

  def forAll_1(p: A => Boolean): Boolean = {
    foldRight(true)((a, b) => p(a) && b)
  }

  def takeWhile_1(p: A => Boolean): Stream[A] = {
//    foldRight(empty)((a, b) => if (p(a)) b else  empty)
    foldRight(empty[A])((h,t) =>
      if (p(h)) cons(h,t)
      else      empty)
  }

  def headOption_1: Option[A] = {
    foldRight(None: Option[A])((a, b) => Some(a))
  }

  def map[B](f: A => B) : Stream[B] = {
    foldRight(empty[B])((h, t) => cons(f(h), t))
  }

  def filter(f: A => Boolean) : Stream[A] = {
    foldRight(empty[A])((h, t) => if (f(h)) cons(h, t) else t)
  }

  def append[B>:A](s: => Stream[B]): Stream[B] =
    foldRight(s)((h,t) => cons(h,t))

  def flatMap[B](f: A => Stream[B]): Stream[B] =
    foldRight(empty[B])((h, t) => f(h) append t)

  def find(p: A => Boolean): Option[A] =
    filter(p).headOption


  }

case object Empty extends Stream[Nothing]
case class SCons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    SCons(() => head, () => tail)
  }

  def constant[A](a: A): Stream[A] = {
    cons(a, constant(a))
  }

  def constant_1[A](a: A): Stream[A] = {
    lazy val tail: Stream[A] = SCons(() => a, () => tail)
    tail
  }

  def from(n: Int): Stream[Int] = {
    cons(n, from(n+1))
  }

  def fibs() : Stream[Int] = {
    def fib(a: Int, b: Int): Stream[Int] = {
      cons(a, fib(b, a+b))
    }
    fib(0, 1)
  }

  def empty[A]: Stream[A] = Empty

  def apply[A](as: A*): Stream[A] =
    if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
}

object chap5 extends App {
  def square(x: Double): Double = x * x

  def if2[A](cond: Boolean, onTrue: () => A, onFalse: () => A): A =
    if (cond) onTrue() else onFalse()

  def maybeTwice(b: Boolean, i: => Int) = if (b) i+i else 0
  def maybeTwice2(b: Boolean, i: => Int) = {
    lazy val j  = i
    if (b) j+j else 0
  }
  val x = maybeTwice2(true, { println("hi"); 1+41 })
  println("x = " + x)

  val a = 33
  if2(a < 22,
    () => println("a"),
    () => println("b")
  )

  println(Stream(1,2,3).take(2).toList)
  println(Stream(2, 3, 9).find(x => x%3 == 0))

  val ones: Stream[Int] = Stream.cons(1, ones)

  println(ones.take(5).toList)
  ones.takeWhile(_ == 1)

  val two = Stream.constant(2)
  println(two.take(5).toList)

  val from1 = Stream.from(1)
  println(from1.take(5).toList)

  val fibt = Stream.fibs()
  println(fibt.take(15).toList)


}