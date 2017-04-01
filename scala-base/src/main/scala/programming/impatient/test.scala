package programming.impatient

import breeze.numerics.pow

/**
  * Created by I311352 on 3/31/2017.
  */

object test extends App{
  val t = Array(3.13, 1.42).map(math.ceil)
  println(t.toString)
  (1 to 10).foreach(println _)

  def until(condition: => Boolean)(block: => Unit): Unit = {
    if (!condition){
      block
      until(condition)(block)
    }
  }

  def indexOf(str: String, ch: Char): Int = {
    var  i = 0
    until(i == str.length) {
      if (str(i) == ch) return i
      i += 1
    }
    return -1
  }

  var x = 10
  until(x == 0) {
    x -= 1
    println(x)
  }


  val names = List("pp")
  val two = names :+ "ok"

  println(two)

  println(List(1, 7, 2, 9).reduceLeft(_ - _))
  println(List(1, 7, 2, 9).reduceRight(_ - _))
  println(List(1, 7, 2, 9).foldLeft(0)(_ - _))

  val freq = scala.collection.mutable.Map[Char, Int]()

  for( c <- "Mississippi") freq(c) = freq.getOrElse(c, 0) + 1
  println(freq)

  (Map[Char, Int]() /: "Mississippi") {
    (m, c) => m + (c-> (m.getOrElse(c, 0) + 1))
  }

  println((1 to 10).scanLeft(0)(_+_))


  val res = List(50.0, 40.0, 9.59) zip List(10, 2)
  println(res.map {p => p._1 * p._2})

  val res2 = List(50.0, 40.0, 9.59) zipAll(List(10, 2), 0.0, 1)
  val powers = (0 until 1000).view.map(pow(10, _))

  println(powers(999))

  def indexes(str: String) : scala.collection.mutable.Map[Char, List[Int]] = {
    var i = 0
    val freq = scala.collection.mutable.Map[Char, List[Int]]()

    for (c <- str) {var ll = freq.getOrElse(c, List()); ll = ll :+ i; freq(c) = ll; i+=1}
    freq
  }

//  def indexes2(str: String) : Map[Char, List[Int]] = {
//
//    str map(ch =>
//  }

  println(indexes("Mississippi"))

}
