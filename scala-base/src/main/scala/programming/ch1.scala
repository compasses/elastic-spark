package programming

/**
  * Created by i311352 on 21/03/2017.
  */
object ch1 {

  def factorial(x:BigInt): BigInt = {
    if (x == 0) 1 else x * factorial(x-1)
  }

  def main(args: Array[String]): Unit ={
    var capital = Map("US" -> "Washington", "France" -> "Paris")
    capital += ("Japan" -> "Tokyo")
    println(capital("France"))
    println(factorial(30))
  }

}
