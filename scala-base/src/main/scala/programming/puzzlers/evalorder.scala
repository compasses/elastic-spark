package programming.puzzlers;

/**
 * Created by i311352 on 27/03/2017.
 */
trait Interface {
  val name: String
  def sayMyName = name+"!"
}
case object Implementation extends Interface {
  override val name = "test"
}


object test extends App {
  println(Implementation.name)
  println(Implementation.sayMyName)
}
