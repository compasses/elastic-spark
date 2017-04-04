package programming.fp;

/**
 * Created by I311352 on 3/28/2017.
 */

import programming.fp
import sun.text.resources.no.CollationData_no

import sys.process._

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

//  def init2[A](l: List[A]): List[A] = {
//    import collection.mutable.ListBuffer
//    val buf = new ListBuffer[A]
//    def go(cur: List[A]): List[A] = cur match {
//      case Nil => sys.error("init of empty list")
//      case Cons(_, Nil) => List(buf.toList: _*)
//      case Cons(h, t) => buf += h; go(t)
//    }
//
//    go(l)
//  }

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

  def foldLeft[A,B](as: List[A], z: B)(f: (B, A) => B): B =
    as match {
      case Nil => z
      case Cons(h, t) => foldLeft(t, f(z,h))(f)
    }

  def appendViaFoldRight[A](l: List[A], r: List[A]): List[A] =
    foldRight(l, r)(Cons(_,_))

  def concat[A](l: List[List[A]]): List[A] =
    foldRight(l, Nil:List[A])(append)

  def sum3(ns: List[Int]) =
    foldLeft(ns, 0)((x,y)=>x+y)

  def product3(ns: List[Int]) =
    foldLeft(ns, 1.0)(_ * _)

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _)

  def length[A](as: List[A]): Int = {
    foldRight(as, 0)((_, y) => y+1)
  }

  def reverse[A](l: List[A]): List[A] = foldLeft(l, List[A]())((acc,h) => Cons(h,acc))

//  def reverse[A](l: List[A]): List[A] = {
//    case Nil => Nil
//    //case Cons(_, Nil) => reverse(l)
//    case Cons(h, t) => Cons(t match {
//        Cons(hh, tt) =>
//    }, Cons(h, Nil))
//    case _ => reverse(l)
//  }

  def addOne(l: List[Int]): List[Int] = {
    l match {
      case Nil => Nil
      case Cons(h,t) => Cons(h+1, addOne(t))
    }
  }

  def toStringDouble(l: List[Double]): List[String] = {
    l match {
      case Nil => Nil
      case Cons(h, t) => Cons(h.toString, toStringDouble(t))
    }
  }

  def map[A,B](as: List[A])(f: A => B): List[B] = {
    as match {
      case Nil => Nil
      case Cons(h, t) => Cons(f(h), map(t)(f))
    }
  }

  def filter_1[A](l: List[A])(f: A => Boolean): List[A] =
    foldRight(l, Nil:List[A])((h,t) => if (f(h)) Cons(h,t) else t)

//  def filter_2[A](l: List[A])(f: A => Boolean): List[A] = {
//    val buf = new collection.mutable.ListBuffer[A]
//    def go(l: List[A]): Unit = l match {
//      case Nil => ()
//      case Cons(h,t) => if (f(h)) buf += h; go(t)
//    }
//
//    go(l)
//    List(buf.toList: _*) // converting from the standard Scala list to the list we've defined here
//  }

  def filter_my[A](l: List[A])(f: A => Boolean): List[A] =
    l match {
      case Nil => Nil
      case Cons(h, t) => if (f(h)) Cons(h, filter_my(t)(f)) else filter_my(t)(f)
    }

  def flatMap[A,B](l: List[A])(f: A => List[B]): List[B] =
    concat(map(l)(f))

  def addPairWise(a: List[Int], b: List[Int]) : List[Int] =
    (a, b) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1+h2, addPairWise(t1, t2))
    }

  def zipWith[A, B, C](a: List[A], b: List[B])(f: (A,B)=>C): List[C] = (a, b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(t1, t2)(f))
  }

  def startsWith[A](l: List[A], prefix: List[A]): Boolean = (l, prefix) match {
    case (_, Nil) => true
    case (Cons(h, t), Cons(h2, t2)) if h == h2 => startsWith(t, t2)
    case _ => false
  }

  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = sup match {
    case Nil => sub == Nil
    case _ if startsWith(sup, sub) => true
    case Cons(_, t) => hasSubsequence(t, sub)
  }

//  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] = {
//    as match {
//      case Nil => Nil
//      case Cons(_, Nil) => Cons(_, Nil)
//      case Cons(h, t) => flatMap(f(h))(f)
//    }
//  }

  def apply[A](as: A*): List[A] = {
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }
}

object chap3 extends App {
  val testleft2 = List.foldLeft(List(1,2,3), 0)((a,b) => a+b)

  println("testleft2 is " + testleft2)

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
  val testleft = List.foldLeft(List(1,2,3), Nil:List[Int])_
  val test3 = List[Int]()
  System.out.println("result list is " + ex12)
  System.out.println("length is " + List.length(xs))
  System.out.println("result sum list is " + List.sum2(xs))
  System.out.println("result sum list is " + List.sum3(xs))

  System.out.println("result product list is " + List.product3(xs))

  System.out.println("test result is " + test)
  System.out.println("testleft result is " + test)
  System.out.println("test3 result is " + test3)
  System.out.println("after add one result is " + List.addOne(xs))

  val xd: List[Double] = List(1.0,2.01,3.03,4.03,5.005)
  val xdm = List.map(xd)(d=>d.toString)

  System.out.println("to string double " + List.toStringDouble(xd))
  System.out.println("to string double from map " + (xdm))

  System.out.println("filter my is " + List.filter_my(xs)(x=>x%2==0))
  System.out.println("filter flatmap is " + List.flatMap(xs)(x=> if (x%2==0) List(x) else Nil))
  System.out.println("zipwith is " + List.zipWith(xs, xd)((a,b)=>a+b))

  //  "ls -al .." !
}


sealed trait Tree[+A]
case object NilT extends Tree[Nothing]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {
  def sizeOfTree[A](t: Tree[A]): Int = t match {
    case NilT => 0
    case Leaf(_) => 1
    case Branch(left, right) => 1 + sizeOfTree(left) + sizeOfTree(right)
  }

  def maximumData(tree: Tree[Int]): Int = tree match {
    case NilT => 0
    case Leaf(x) => x
    case Branch(left, right) => maximumData(left) max maximumData(right)
  }

  def depthTree[A](tree: Tree[A]): Int = tree match {
    case NilT => 0
    case Leaf(_) => 1
    case Branch(left, right) => depthTree(left) max depthTree(right)
  }

  def map[A, B](tree: Tree[A])(f: A => B): Tree[B] = tree match {
    case NilT => NilT
    case Leaf(x) => Leaf(f(x))
    case Branch(left, right) => Branch(map(left)(f), map(right)(f))
  }

  /**
    * Like `foldRight` for lists, `fold` receives a "handler" for each of the data constructors of the type,
    * and recursively accumulates some value using these handlers. As with `foldRight`, `fold(t)(Leaf(_))(Branch(_,_)) == t`,
    * and we can use this function to implement just about any recursive function that would otherwise be defined by pattern matching.
    * @param t
    * @param f
    * @param g
    * @tparam A
    * @tparam B
    * @return
    */
  def fold[A, B](t: Tree[A])(f: A => B)(g: (B, B) => B): B = t match {
    case Leaf(a) => f(a)
    case Branch(l, r) => g(fold(l)(f)(g), fold(r)(f)(g))
  }

  def sizeWithFold[A](tree: Tree[A]) : Int = {
    fold(tree)(a=>1)(1+_+_)
  }

  def maximumWithFold(tree: Tree[Int]): Int = {
    fold(tree)(a => a)(_ max _)
  }

  def depthViaFold[A](tree: Tree[A]): Int = {
    fold(tree)(a => 0)((r, l) => 1+ (r max l))
  }

  def mapViaFold[A,B](t: Tree[A])(f: A => B): Tree[B] =
    fold(t)(a => Leaf(f(a)): Tree[B])(Branch(_,_))

}