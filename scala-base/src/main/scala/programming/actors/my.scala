package programming.actors


import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

/**
  * Created by I311352 on 4/10/2017.
  */

class MyActors extends Actor {
  override def receive = {
    case value : String => println("Got " + value)
    case _ => println("received unknown message")
   }
}

case class ProcessStringMsg(string: String)
case class StringProcessedMsg(words: Integer)

class StringCounterActor extends Actor {
  def receive = {
    case ProcessStringMsg(string) => {
      println("got word line " + string)
      val wordsInLine = string.split(" ").length
      sender ! StringProcessedMsg(wordsInLine)
    }
    case _ => println("Error: message not recognized")
  }
}

case class StartProcessFileMsg()

class WordCountActor(filename: String) extends Actor {
  private var running = false
  private var totalLines = 0
  private var linesProcessed = 0
  private var result = 0
  private var fileSender : Option[ActorRef] = None

  override def receive = {
    case StartProcessFileMsg() => {
      if (running) {
        println("Warning: duplicate start message received")
      } else {
        running = true
        fileSender = Some(sender)
        import scala.io.Source._
        println("Going load file " + filename)
        fromFile(filename).getLines().foreach { line =>
          context.actorOf(Props[StringCounterActor]) ! ProcessStringMsg(line)
          totalLines += 1
        }
      }
    }
    case StringProcessedMsg(words) => {
      println("got words " + words)
      result += words
      linesProcessed += 1
      if (linesProcessed == totalLines) {
        fileSender.map(_ ! result)
      }
    }
    case _ => println("Message not recognized")
  }
}

object Sample extends App {

  import akka.util.Timeout
  import scala.concurrent.duration._
  import akka.pattern.ask
  import akka.dispatch.ExecutionContexts._

  implicit val ec = global

  override def main(args: Array[String]) {
    val system = ActorSystem("System")
    val actor = system.actorOf(Props(new WordCountActor("C:\\AnyWhere\\Docker File\\apache2")))
    implicit val timeout = Timeout(25 seconds)
    val future = actor ! StartProcessFileMsg()

    println("last thing...")
//    future.map { result =>
//      println("Total number of words " + result)
//      system.terminate()
//    }
    import scala.reflect.runtime.universe._

    def checkType[A: TypeTag](a: A, t: Type) = typeOf[A] <:< t
  }
}

case class NumSeq(num:Seq[Int])


class CountActor1 extends Actor with ActorLogging {

  def receive = {
    case nums:Seq[Int] => {
      log.info("My path is " + self.path)
      log.info(" parent is" + context.parent.path)

      log.info("Start counting")
      val rest = nums.foldLeft(0)(_+_)
      sender ! rest
    }
    case _ => log.info("Nothing got...")
  }
}

class SumCountActor extends Actor with ActorLogging {
  // private
  val countActor = context.actorOf(Props[CountActor1], "count-actor")
  private var sum = 0;

  def receive = {
    case "StartCount" => {
      log.info("My path is " + self.path)
      log.info("Starting counting")
    }

    case sq:Seq[Int] => {
      log.info("My path is " + self.path)
      log.info(" parent is" + context.parent.path)

      log.info("receive " + sq)
      Thread.sleep(1)
      countActor ! sq
    }

    case re:Int => {
      sum += re
      log.info("Got result " + sum)
    }
  }
}

object ActorTest extends App {
  val system = ActorSystem("counter-system")
  system.log.info(system.toString)

  val sumActorRef = system.actorOf(Props[SumCountActor], name="sum-actor")
  //val countActorRef = system.actorOf(Props[CountActor1], name="count-actor")

  //while (true) {
    sumActorRef ! Seq(1,2,3,4,5)
    Thread.sleep(2)
  sumActorRef ! Seq(1,2,3,4,5)
  sumActorRef ! Seq(1,2,3,4,5)

  //}

}

