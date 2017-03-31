package programming.impatient

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

}
