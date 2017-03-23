import programming.Network

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

def abs(x: Double) = if (x >= 0) x else -x

println(abs(-23))


def decorate(str:String, left:String = "[", right:String = "]") = {
  left + str + right
}

decorate("Heel")

val s = Array("hello", "World")
s(0) = "dd"
println(s(0))

val a = ArrayBuffer(0, 1, 2, -1, 4, 4, 5, -3, 10, 11, -5, 56, -9)

var first = true
val indexes = for (i <- 0 until a.length if first || a(i) >= 0) yield {
  if (a(i) < i) first = false; i
}

for (j <- 0 until indexes.length) a(j) = a(indexes(j))

a.trimEnd(a.length - indexes.length)
a

val martrix = Array.ofDim[Double](3,4)

martrix

val triangle  = new Array[Array[Int]](10)
for (i <- 0 until triangle.length)
  triangle(i) = new Array[Int](i+1)

triangle

"Alice" -> 10

val months = mutable.LinkedHashMap("J" -> 1)

"New York".partition(_.isUpper)


//val chatter = new Network
//val myFace = new Network
//
//val fred = chatter.join("Fred")