package programming.fp_part1

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

  def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = {
    f(z) match {
      case Some((h,s)) => cons(h, unfold(s)(f))
      case None => empty
    }
  }
//  def mapViaUnfold[B](f: A => B): Stream[B] =
//    unfold(this) {
//      case Cons(h,t) => Some((f(h()), t()))
//      case _ => None
//    }
//
//  def takeViaUnfold(n: Int): Stream[A] =
//    unfold((this,n)) {
//      case (Cons(h,t), 1) => Some((h(), (empty, 0)))
//      case (Cons(h,t), n) if n > 1 => Some((h(), (t(), n-1)))
//      case _ => None
//    }
//
//  def takeWhileViaUnfold(f: A => Boolean): Stream[A] =
//    unfold(this) {
//      case Cons(h,t) if f(h()) => Some((h(), t()))
//      case _ => None
//    }
//
//  def zipWith[B,C](s2: Stream[B])(f: (A,B) => C): Stream[C] =
//    unfold((this, s2)) {
//      case (Cons(h1,t1), Cons(h2,t2)) =>
//        Some((f(h1(), h2()), (t1(), t2())))
//      case _ => None
//    }

//  val fibsViaUnfold =
//    unfold((0,1)) { case (f0,f1) => Some((f0,(f1,f0+f1))) }

  def fromViaUnfold(n: Int) =
    unfold(n)(n => Some((n,n+1)))

  def constantViaUnfold[A](a: A) =
    unfold(a)(_ => Some((a,a)))

  // could also of course be implemented as constant(1)
  //val onesViaUnfold = unfold(1)(_ => Some((1,1)))

//  def zipWith[B,C](s2: Stream[B])(f: (A,B) => C): Stream[C] =
//    unfold((this, s2)) {
//      case (Cons(h1,t1), Cons(h2,t2)) =>
//        Some((f(h1(), h2()), (t1(), t2())))
//      case _ => None
//    }

//  def zipAll[B](s2: Stream[B]): Stream[(Option[A],Option[B])] =
//    zipWithAll(s2)((_,_))

  // special case of `zipWith`
//  def zip[B](s2: Stream[B]): Stream[(A,B)] =
//    zipWith(s2)((_,_))

//  def zipWithAll[B, C](s2: Stream[B])(f: (Option[A], Option[B]) => C): Stream[C] =
//    Stream.unfold((this, s2)) {
//      case (Empty, Empty) => None
//      case (Cons(h, t), Empty) => Some(f(Some(h()), Option.empty[B]) -> (t(), empty[B]))
//      case (Empty, Cons(h, t)) => Some(f(Option.empty[A], Some(h())) -> (empty[A] -> t()))
//      case (Cons(h1, t1), Cons(h2, t2)) => Some(f(Some(h1()), Some(h2())) -> (t1() -> t2()))
//    }
//
//  def startsWith[A](s: Stream[A]): Boolean =
//  zipAll(s).takeWhile(!_._2.isEmpty) forAll {
//    case (h,h2) => h == h2
//  }

  def tails: Stream[Stream[A]] =
    unfold(this) {
      case Empty => None
      case s => Some((s, s drop 1))
    } append Stream(empty)

  def scanRight[B](z: B)(f: (A, => B) => B): Stream[B] =
    foldRight((z, Stream(z)))((a, p0) => {
      // p0 is passed by-name and used in by-name args in f and cons. So use lazy val to ensure only one evaluation...
      lazy val p1 = p0
      val b2 = f(a, p1._1)
      (b2, cons(b2, p1._2))
    })._2

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

  val list = Stream(1,2,3).scanRight(0)(_ + _).toList
  println("List is " + list)
}