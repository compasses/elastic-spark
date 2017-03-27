package programming.coconvariant

/**
  * Created by i311352 on 27/03/2017.
  */
//https://blog.codecentric.de/en/2015/03/scala-type-system-parameterized-types-variances-part-1/
abstract class Fruit { def name: String }

class Orange extends Fruit { def name = "Orange" }

class Apple extends Fruit { def name = "Apple" }

abstract class Box {

  def fruit: Fruit

  def contains(aFruit: Fruit) = fruit.name.equals(aFruit.name)
}

class OrangeBox(orange: Orange) extends Box {
  def fruit: Orange = orange
}

class AppleBox(apple: Apple) extends Box {
  def fruit: Apple = apple
}

class BoxT[+F <: Fruit](aFruit: F) {

  def fruit: F = aFruit

  def contains(aFruit: Fruit) = fruit.name == aFruit.name
}

object convariant extends App {

  var appleBox = new BoxT[Apple](new Apple)

  var orangeBox = new BoxT[Orange](new Orange)

  var box: BoxT[Fruit] = new BoxT[Apple](new Apple)

  var apple = new Apple
  var boxs: Box = new AppleBox(apple)
  println("box contains an $boxs.fruit.name")

}
