package programming.impatient

import scala.collection.generic.CanBuildFrom

/**
  * Created by I311352 on 4/11/2017.
  */
object conversion extends App {
  case class Delimiters(left: String, right: String)
  def quote(what: String)(implicit delims: Delimiters) =
    delims.left + what + delims.right

  println(quote("okok")(Delimiters("<<", ">>")))
  implicit val quoteDelimiter = Delimiters("((", "))")
  println(quote("okok222"))

  def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (a<b) a else b

  println(smaller(12,13))

  def firstLast[A,C](it:C)(implicit ev : C <:< Iterable[A]) = (it.head, it.last)


  val ll = firstLast[Int, List[Int]](List(1, 2, 4))

  println(ll)

//  def map2[B, That](f: A => B)(implicit bf: CanBuildFrom[Repr, B, That]): That = {
//    val builder = bf()
//    val iter = iterator()
//    while (iter.hasNext) builder += f(iter.next())
//
//    builder.result()
//  }

}
