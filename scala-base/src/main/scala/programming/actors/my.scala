package programming.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

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
  }
}

object ActorTest extends App {

}