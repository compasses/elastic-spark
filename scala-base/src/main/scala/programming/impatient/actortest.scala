package programming.impatient

import akka.actor.{Actor, ActorSystem, Props}
import programming.actors.WordCountActor


/**
  * Created by I311352 on 4/7/2017.
  */

class HiActor extends Actor {
  def receive = {
    case "Hi" => println("Hello")
          sender ! "back"
    case _ => println("No recognized msg")
  }
}

object actortest extends App {
  val system = ActorSystem("System")
  val actor = system.actorOf(Props(new HiActor))
  val back = actor ! "Hi"
  println(back)
  actor ! "Yes it's right"
  while (true)
    actor ! "Hi"
//  future.map { result =>
//    println("Total number of words " + result)
//    system.terminate()
//  }
}
