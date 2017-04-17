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

trait Logger {
  def log(msg: String)
  def warn(msg: String) {log("WARN " + msg)}
}

trait TimestampLogger extends Logger {
  abstract override def log(msg: String) {
    super.log(new java.util.Date() + " " + msg)
  }
}

abstract class Account {
  var banlance : Int;
}

class SaveAccount extends Account with Logger {
  var banlance: Int = 10;

  def output(): Unit = {
    warn("barabatr")
  }

  override def log(msg: String) {
    println(msg)
  }
}

object SaveAccount extends App {
  val saveCount = new SaveAccount with TimestampLogger
  saveCount.output()

  var any: Any = _
  println (any match {
    case "scala" | "java" => "string"
    case i: Int if i > 10 => "int > 10"
    //case `str` => "abcdef"
    case _: String => "string type"
    case hd :: tail => "List"
    case any => any
    case _ => "other"
  })
}