package programming.impatient

import akka.actor.{Actor, ActorSystem, Props}
import programming.actors.WordCountActor


/**
  * Created by I311352 on 4/7/2017.
  */

class HiActor extends Actor {
  def receive = {
    case "Hi" => println("Hello")
    case _ => println("No recognized msg")
  }
}

object actortest extends App {
  val system = ActorSystem("System")
  val actor = system.actorOf(Props(new HiActor))
  actor ! "Hi"
  actor ! "Yes it's right"
//  future.map { result =>
//    println("Total number of words " + result)
//    system.terminate()
//  }
}
