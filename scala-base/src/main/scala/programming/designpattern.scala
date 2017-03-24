package programming

import java.io.ByteArrayOutputStream

import org.apache.log4j.Level

/**
  * Created by i311352 on 24/03/2017.
  */
class designpattern {

}


// factory pattern
trait Animal
private class Cat extends Animal
private class Dog extends Animal


object Animal {
  def apply(kind: String) = kind match {
    case "dog" => new Dog()
    case "cat" => new Cat()
  }
}


object designpattern extends App{
  val a = Animal("dog")
  println(a.isInstanceOf[Dog])

  // lazy initialization; thread-safe
  lazy val x = {
    println("computing x")
    45
  }

  print("x = ")
  println(x)
}

//single pattern
object CatTest extends Runnable {
  override def run() = {
    // do nothing
  }

  def main(args: Array[String]): Unit = {
    CatTest.run()
    println("running ...")
  }
}


object AdapterPattern extends App{

  trait Log {
    def warning(message: String)
    def error(message: String)
  }

  final class Logger{
    def log(level: Level, message: String): Unit = {

    }
  }

  implicit class LoggerToLogAdapter(logger: Logger) extends Log {
    def warning(message: String) { logger.log(Level.WARN, message) }
    def error(message: String) { logger.log(Level.ERROR, message) }
  }

  val log: Log = new Logger

  log.error("eeeeeoro")
}

object DecoratorPattern extends App {
  trait OutputStream {
    def write(b: Byte)
    def wtrite(b: Array[Byte])
  }

  class FileOutputStream(path: String) extends OutputStream {
    override def write(b: Byte) = {
      println("Yes, i am writing ..." + b)

    }

    override def wtrite(b: Array[Byte]) = {
    println("Yes, i am writing array ...")
    }
  }

  trait Buffering extends OutputStream {
    abstract override def write(b: Byte) = {
      println("Before write in trait ..." + b)
      super.write(b)
    }
  }
  val fileOutputStream = new FileOutputStream("foo.txt") with Buffering // with Filtering, ...
  fileOutputStream.write(111)

  // value object

  val point1 = (1, 2)
  val point2 = (1, 2)

  println(point1 == point2)

  case class Point(x: Int, y: Int)

  val point = Point(1, 2)

  println(point)
}

object nullobjec {
  trait Sound {
    def play()
  }

  class Music extends Sound {
    def play() { /* ... */ }
  }

  object SoundSource {
    def getSound: Option[Sound] =
      if (false) Some(new Music) else None
  }

  for (sound <- SoundSource.getSound) {
    sound.play()
  }
}

object strategyobj extends App {
  type Strategy = (Int, Int) => Int

  class Context(computer: Strategy) {
    def use(a: Int, b: Int) = { computer(a, b) }
  }

  val add: Strategy = _ + _
  val multiply: Strategy = _ * _

  val re = new Context(multiply).use(2, 3)

  println(re)
}

object Invoker extends App {
  private var history: Seq[() => Unit] = Seq.empty

  def invoke(command: => Unit) { // by-name parameter
    command
    history :+= command _
  }

  Invoker.invoke(println("foo"))

  Invoker.invoke {
    println("bar 1")
    println("bar 2")
  }
}

object chainresponsibility extends App {
  case class Event(source: String)

  type EventHandler = PartialFunction[Event, Unit]

  val defaultHandler: EventHandler = PartialFunction(_ => ())

  val keyboardHandler: EventHandler = {
    case Event("keyboard") => /* ... */
  }

  def mouseHandler(delay: Int): EventHandler = {
    case Event("mouse") => /* ... */
  }

  keyboardHandler.orElse(mouseHandler(100)).orElse(defaultHandler)
}

object dependencyinj extends App {
  trait User

  trait Repository {
    def save(user: User)
  }

  trait DatabaseRepository extends Repository {
    override def save(user: User) = {
      println("Going to save ...")
    }
  }

  trait UserService { self: Repository => // requires Repository
  def create(user: User) {
    // ...
    save(user)
  }
  }

  new UserService with DatabaseRepository
}