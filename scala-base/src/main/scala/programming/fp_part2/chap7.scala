package programming.fp_part2

import java.util.concurrent.{Callable, CountDownLatch, ExecutorService}
import java.util.concurrent.atomic.AtomicReference


/**
  * Created by I311352 on 4/6/2017.
  */
sealed trait Future[A] {
  private[fp_part2] def apply(k: A => Unit): Unit
  type Par[+A] = ExecutorService => Future[A]

  def run[A](es: ExecutorService)(p: Par[A]): A = {
    val ref = new AtomicReference[A]
    val latch = new CountDownLatch(1)
    p(es) { a => ref.set(a); latch.countDown }
    latch.await
    ref.get
  }

  def unit[A](a: A): Par[A] =
    es => new Future[A] {
      def apply(cb: A => Unit): Unit =
        cb(a)
    }

  def fork[A](a: => Par[A]): Par[A] =
    es => new Future[A] {
      def apply(cb: A => Unit): Unit =
        eval(es)(a(es)(cb))
    }

  def eval(es: ExecutorService)(r: => Unit): Unit =
    es.submit(new Callable[Unit] { def call = r })

  def map2[A,B,C](p1: Par[A], p2: Par[B])(f: (A, B) => C): Par[C] = {
    es => new Future[C] {
      override private[fp_part2] def apply(cb: (C) => Unit) = {
        var ar : Option[A] = None
        var br : Option[B] = None
        var combiner = Actor[Either[A, B]](es) {
          case Left(a) => br match {
            case None => ar = Some(a)
            case Some(b) => eval(es)(cb(f(a, b)))
          }
          case Right(b) => ar match {
            case None => br = Some(b)
            case Some(a) => eval(es)(cb(f(a, b)))
          }
        }
        p1(es)(a => combiner ! Left(a))
        p2(es)(b => combiner ! Right(b))
      }
    }
  }

}


object chap7 extends App {
  def sum(ints: Seq[Int]): Int =
    ints.foldLeft(0)((a,b) => a + b)

  def sum(ints: IndexedSeq[Int]): Int =
    if (ints.size <= 1)
      ints.headOption getOrElse 0
    else {
      val (l,r) = ints.splitAt(ints.length/2)
      sum(l) + sum(r)
    }

  println("Sum is " + sum(Array(1, 2, 3)))
  //val p = parMap(List.range(1, 100000))(math.sqrt(_))
  //def run[A](s: ExecutorService)(a: Par[A]): Future[A] = a(s)

  /* def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C): Par[C] */
//  trait Par[+A]
//  case class Con[+A](native: A) extends Par[A]
//
//  def unit[A](a: A): Par[A]
//  def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C): Par[C]
//  def fork[A](a: => Par[A]): Par[A]
//  def lazyUnit[A](a: => A): Par[A] = fork(unit(a))
//  def run[A](a: Par[A]): A
  // def run[A](s: ExecutorService)(a: Par[A]): A
}

//object Par {
//  type Par[A] = ExecutorService => Future[A]
//
//  def unit[A](a: A): Par[A] = (es: ExecutorService) => UnitFuture(a)
//
//  private case class UnitFuture[A](get: A) extends Future[A] {
//    def isDone = true
//    def get(timeout: Long, units: TimeUnit) = get
//    def isCancelled = false
//    def cancel(evenIfRunning: Boolean): Boolean = false
//  }
//  def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C): Par[C] =
//    es => {
//      val (af, bf) = (a(es), b(es))
//      Map2Future(af, bf, f)
//    }
//
////  def map2[A,B,C](a: Par[A], b: Par[B])(f: (A,B) => C): Par[C] =
////    (es: ExecutorService) => {
////      val af = a(es)
////      val bf = b(es)
////      UnitFuture(f(af.get, bf.get))
////    }
////  def asyncF[A,B](f: A => B): A => Par[B] =
////    a => lazyUnit(f(a))
//
//  def fork[A](a: => Par[A]): Par[A] =
//    es => es.submit(new Callable[A] {
//      def call = a(es).get
//    })
//
//  case class Map2Future[A,B,C](a: Future[A], b: Future[B],
//                               f: (A,B) => C) extends Future[C] {
//    @volatile var cache: Option[C] = None
//    def isDone = cache.isDefined
//    def isCancelled = a.isCancelled || b.isCancelled
//    def cancel(evenIfRunning: Boolean) =
//      a.cancel(evenIfRunning) || b.cancel(evenIfRunning)
//    def get = compute(Long.MaxValue)
//    def get(timeout: Long, units: TimeUnit): C =
//      compute(TimeUnit.NANOSECONDS.convert(timeout, units))
//
//    private def compute(timeoutInNanos: Long): C = cache match {
//      case Some(c) => c
//      case None =>
//        val start = System.nanoTime
//        val ar = a.get(timeoutInNanos, TimeUnit.NANOSECONDS)
//        val stop = System.nanoTime;val aTime = stop-start
//        val br = b.get(timeoutInNanos - aTime, TimeUnit.NANOSECONDS)
//        val ret = f(ar, br)
//        cache = Some(ret)
//        ret
//    }
//  }
////  def sortPar(parList: Par[List[Int]]): Par[List[Int]]
////
////  def sortPar(parList: Par[List[Int]]): Par[List[Int]] =
////    map2(parList, unit(()))((a, _) => a.sorted)
//
//  def sequenceRight[A](as: List[Par[A]]): Par[List[A]] =
//    as match {
//      case Nil => unit(Nil)
//      case h :: t => map2(h, fork(sequenceRight(t)))(_ :: _)
//    }
//
////  def parFilter[A](l: List[A])(f: A => Boolean): Par[List[A]] = {
////    val pars: List[Par[List[A]]] =
////      l map (asyncF((a: A) => if (f(a)) List(a) else List()))
////    map2(sequence(pars))(_.flatten) // convenience method on `List` for concatenating a list of lists
////  }
//
//}