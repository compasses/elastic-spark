package programming

import scala.collection.mutable.ArrayBuffer

/**
  * Created by i311352 on 22/03/2017.
  */
class classTest {


}

class Network {
  class Members(val name: String) {
    val contacts = new ArrayBuffer[Members]
  }

  val members = new ArrayBuffer[Members]

  def join(name: String) = {
    val m = new Members(name)
    members += m
    m
  }

  def print(): Unit = {
    println(members)
  }


}

object test{
  def main(args: Array[String]): Unit = {
    val chatter = new Network
    val myFace = new Network

    val fred = chatter.join("Fred")
    fred.contacts
    chatter.print()
    myFace.print()
  }
}
