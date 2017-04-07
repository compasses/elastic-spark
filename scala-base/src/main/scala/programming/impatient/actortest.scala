package programming.impatient

import akka.actor.Actor


/**
  * Created by I311352 on 4/7/2017.
  */

class HiActor extends Actor {
  def receive = {
    case "Hi" => println("Hello")
  }

}

object actortest extends App {
  val actor1 = new HiActor
  actor1.preStart()
}
