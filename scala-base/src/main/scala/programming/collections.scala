package programming

/**
  * Created by i311352 on 25/03/2017.
  */

class collections {

}

object collections extends App {
  Traversable(1, 2, 3)
  val str = 1 #:: 2 #:: 3 #:: Stream.empty

  println(str(2))

  def fibFrom(a: Int, b: Int): Stream[Int] = a #:: fibFrom(b, a + b)
  val fibs = fibFrom(1, 1).take(7)
  println(fibs.toList)
  val buf = scala.collection.mutable.ArrayBuffer.empty[Int]

  def evenElems[T: ClassManifest](xs: Vector[T]): Array[T] = {
    val arr = new Array[T]((xs.length + 1) / 2)
    for (i <- 0 until xs.length by 2)
      arr(i / 2) = xs(i)
    arr
  }
  val a1 = Array(1, 2, 3)

  println(Vector(1, 2, 3, 4, 5))
}