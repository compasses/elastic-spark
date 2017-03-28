package programming.fp;

/**
 * Created by I311352 on 3/28/2017.
 */

sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def tail[A](as: List[A]): List[A] = as match {
    case Nil => Nil
    case Cons(_, xs) => xs
  }

  def setHead[A](as: List[A], newh: A) : List[A] = as match {
    case Nil => Cons(newh, Nil)
    case Cons(x, xs) => Cons(newh, xs)
  }

  def drop[A](l: List[A], n: Int): List[A] = {
    if (n <= 0) {
      l
    } else {
      l match {
        case Nil => Nil
        case Cons(_, t) => drop(t, n-1)
      }
    }
  }

  def init3[A](l: List[A]): List[A] = {
    l match {
      case Nil => sys.error("init of empty list")
      case Cons(_, Nil) => Nil
      case Cons(h, t) => Cons(h, init3(t))
    }
  }

  def init[A](l: List[A]): List[A] =
  l match {
    case Nil => sys.error("init of empty list")
    case Cons(_, Nil) => Nil
    case Cons(h, t) => Cons(h, init(t))
  }

  def init2[A](l: List[A]): List[A] = {
    import collection.mutable.ListBuffer
    val buf = new ListBuffer[A]
    def go(cur: List[A]): List[A] = cur match {
      case Nil => sys.error("init of empty list")
      case Cons(_, Nil) => List(buf.toList: _*)
      case Cons(h, t) => buf += h; go(t)
    }

    go(l)
  }

  def dropWhile2[A](as: List[A])(f: A => Boolean): List[A] =
    as match {
      case Cons(h,t) if f(h) => dropWhile2(t)(f)
      case _ => as
    }

  def dropWhile[A](as: List[A])(f: A => Boolean): List[A] =
    as match {
      case Cons(h,t) if f(h) => dropWhile(t)(f)
      case _ => as
    }

  def dropWhile3[A](as: List[A], f: A => Boolean): List[A] = {
    as match {
      case Cons(h, t) if f(h) => dropWhile3(t, f)
      case _ => as
    }
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B =
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _)

  def length[A](as: List[A]): Int = {
    foldRight(as, 0)((_, y) => y+1)
  }

  def foldLeft[A,B](as: List[A], z: B)(f: (B, A) => B): B = {
    as match {
      case Nil => z
      case Cons(x, xs) => f(foldLeft(xs, z)(f), x)
    }
  }

  def apply[A](as: A*): List[A] = {
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }
}

object chap3 extends App {
  val ex1: List[Double] = Nil
  val ex2: List[Int] = Cons(1, Nil)
  val ex3: List[String] = Cons("a", Cons("b", Nil))
  System.out.println("ex3 is " + ex3)

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + List.sum(t)
    case _ => 101
  }

  System.out.println("result is " + x)
  val xs: List[Int] = List(1,2,3,4,5)
  val ex12 = List.dropWhile3(xs, (x: Int) => x < 4)
  val ex13 = List.dropWhile(xs)(x=>x<4)
  val test = List.foldRight(List(1,2,3), Nil:List[Int])(Cons(_,_))


  System.out.println("result list is " + ex12)
  System.out.println("length is " + List.length(xs))
  System.out.println("result sum list is " + List.sum2(xs))
  System.out.println("result is " + test)
}


sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]