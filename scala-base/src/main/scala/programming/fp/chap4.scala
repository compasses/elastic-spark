package programming.fp

import scala.util.hashing.Hashing.Default

/**
  * Created by I311352 on 3/30/2017.
  */
class chap4 {

}

object chap4 extends App {
  def failingFn(i: Int): Int = {
    val y: Int = throw new Exception("fail!")
    try {
      val x = 42 + 5
      x + y
    }
    catch { case e: Exception => 43 }
  }

  //failingFn(12)
  def failingFn2(i: Int): Int = {
    try {
      val x = 42 + 5
      x + ((throw new Exception("fail!")): Int)
    }
    catch { case e: Exception => 43 }
  }

  def mean(xs: Seq[Double]): Double =
    if (xs.isEmpty)
      throw new ArithmeticException("mean of empty list!")
    else xs.sum / xs.length

  def mean_1(xs: IndexedSeq[Double], onEmpty: Double): Double =
    if (xs.isEmpty) onEmpty
    else xs.sum / xs.length

  println(failingFn2(12))
}

sealed trait Option[+A] {
  def map[B](f: A => B) : Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }

  def flatMap[B](f: A => Option[B]) : Option[B] = {
    map(f) getOrElse None
  }

  def getOrElse[B >: A](default: => B) : B = this match {
    case None => default
    case Some(a) => a
  }

  def orElse[B >: A](ob: => Option[B]) : Option[B] = this match {
    case None => ob
    case _ => this
  }

  def filter(f: A => Boolean) : Option[A] = this match {
    case Some(a) if f(a) => this
    case _ => None
  }

  def orElse_1[B>:A](ob: => Option[B]): Option[B] =
    this map (Some(_)) getOrElse ob

  def flatMap_1[B](f: A => Option[B]): Option[B] = this match {
    case None => None
    case Some(a) => f(a)
  }
}

case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

object testOption {
  def variance(xs: Seq[Double]): Option[Double] = {
    mean(xs) flatMap (m=> mean(xs.map(x => math.pow(x - m, 2))))
  }

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  def lift[A,B](f: A => B): Option[A] => Option[B] = _ map f

  def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] =
    a flatMap (aa => b map (bb => f(aa, bb)))

//  def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a, b) match {
//    case (None, _) => None
//    case (_, None) => None
//    case (x, y)    => Some(f(a.getOrElse(None), b.getOrElse(None)))
//  }

  def insuranceRateQuote(age: Int, numberOfSpeedingTickets: Int): Double = {
    1.0
  }

  def parseInsuranceRateQuote(age: String, numberOfSpeedingTickets: String): Option[Double] = {
    val optAge = Try(age.toInt)
    val optTickets = Try(numberOfSpeedingTickets.toInt)
    map2(optAge, optTickets)(insuranceRateQuote)
  }

  def Try[A](a: => A) : Option[A] = {
    try
      Some(a)
    catch {case e: Exception => None   }
  }

  def sequence_1[A](a: List[Option[A]]): Option[List[A]] =
    a match {
      case Nil => Some(Nil)
      case Cons(h, t) => h flatMap (hh => sequence_1(t) map (Cons(hh , _)))
    }

//  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
//    a map f(aa => f(aa))
//  }

  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
    a match {
      case Nil => Some(Nil)
      case Cons(h, t) => map2(f(h), traverse(t)(f))(Cons(_ , _))
    }

//  def traverse_1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] =
//    a foldRight[Option[List[B]]](Some(Nil))((h,t) => map2(f(h),t)(Cons(_, _)))

  def sequenceViaTraverse[A](a: List[Option[A]]): Option[List[A]] =
    traverse(a)(x => x)

  def sequence[A](a: List[Option[A]]): Option[List[A]] = a match {
    case Nil => None
    //case Cons(h, t) => if (h == None) None else Some(Cons(h getOrElse(None), sequence(t)))
  }

  def map2_1[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C):
  Option[C] =
    for {
      aa <- a
      bb <- b
    } yield f(aa, bb)


//  def parseInts(a: List[String]): Option[List[Int]] =
//    sequence_1(a map(i => Try(i.toInt)))

  def main(args: Array[String]): Unit = {
    val testArr = Array(1.0, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val value = variance(testArr) getOrElse 0.0
    println("variance is " + value )

    val absO: Option[Double] => Option[Double] = lift(math.abs)
    println("absO " + absO(Some(-1.2)))
  }
}

trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B] = this match {
    case Right(a) => Right(f(a))
    case Left(e) => Left(e)
  }

  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
    case Right(a) => f(a)
    case Left(e) => Left(e)
  }

  def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = this match {
    case Left(e) => b
    case Right(a) => Right(a)
  }

  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C) : Either[EE, C] =  {
    for { a <- this; b1 <- b } yield f(a, b1)
  }

  def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]] = traverse(es)(x => x)

  def traverse[E, A, B](as: List[A])(f: A => Either[E, B]): Either[E, List[B]] = as match {
    case Nil => Right(Nil)
    case Cons(h, t) => (f(h) map2 traverse(t)(f))(Cons(_, _))
  }
}

case class Left[+E](value: E) extends Either[E, Nothing]
case class Right[+A](value: A) extends Either[Nothing, A]

object EitherTest {
  def mean(xs: IndexedSeq[Double]): Either[String, Double] =
    if (xs.isEmpty)
      Left("mean of empty list!")
    else
      Right(xs.sum / xs.length)

  def safeDiv(x: Int, y: Int): Either[Exception, Int] =
    try Right(x / y)
    catch { case e: Exception => Left(e) }

  def Try[A](a: => A): Either[Exception, A] =
    try Right(a)
    catch { case e: Exception => Left(e) }
}

case class Person(name: Name, age: Age) {
  def mkName(name: String): Either[String, Name] =
    if (name == "" || name == null) Left("Name is empty.")
    else Right(new Name(name))

  def mkAge(age: Int): Either[String, Age] =
    if (age < 0) Left("Age is out of range.")
    else Right(new Age(age))

  def mkPerson(name: String, age: Int): Either[String, Person] =
    mkName(name).map2(mkAge(age))(Person(_, _))
}

sealed class Name(val value: String)
sealed class Age(val value: Int)

trait Partial[+A,+B]
case class Errors[+A](get: Seq[A]) extends Partial[A, Nothing]
case class Success[+B](get: B) extends Partial[Nothing, B]
