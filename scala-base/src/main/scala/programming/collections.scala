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


  trait Service
  def make() = new Service {
    def getId = 123
  }

  class Covariant[+A]
  val cv: Covariant[AnyRef] = new Covariant[String]

  class Animal { val sound = "rustle" }
  class Bird extends Animal { override val sound = "call" }

  class Chicken extends Bird { override val sound = "cluck" }
  val getTweet: (Bird => String) = ((a: Animal) => a.sound )
  val hatch: (() => Bird) = (() => new Chicken )
  //def cacophony[T](things: Seq[T]) = things map (_.sound)
  def biophony[T <: Animal](things: Seq[T]) = things map (_.sound)

  biophony(Seq(new Chicken, new Bird))
  val flock = List(new Bird, new Bird)
  var endList = new Chicken :: flock

  println(endList)
  var endList2 = new Animal :: flock
  println(endList2)

  def count(l: List[_]) = l.size
  println("size :" + count(endList2))

  def drop1(l: List[_]) = l.tail
  def drop2(l: List[T forSome { type T }]) = l.tail

  def hashcodes(l: Seq[_ <: AnyRef]) = l map (_.hashCode)
  hashcodes(Seq("one", "two", "three"))
}

