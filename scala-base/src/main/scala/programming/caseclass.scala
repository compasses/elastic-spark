package programming

/**
  * Created by i311352 on 23/03/2017.
  */


trait Similarity {
  def isSimilar(x: Any) : Boolean
  def isNotSimilar(x: Any) : Boolean = !isSimilar(x)
}

class TraitPoint(xc: Int, yc: Int) extends Similarity {
  var x: Int = xc
  var y: Int = yc

  override def isSimilar(obj: Any) = {
    obj.isInstanceOf[TraitPoint] &&
    obj.asInstanceOf[TraitPoint].x == x
  }
}
object TraitsTest extends App {
  val p1 = new TraitPoint(2, 3)
  val p2 = new TraitPoint(2, 4)
  val p3 = new TraitPoint(3, 3)
  val p4 = new TraitPoint(2, 3)
  println(p1.isSimilar(p2))
  println(p1.isSimilar(p3))
  // Point's isNotSimilar is defined in Similarity
  println(p1.isNotSimilar(2))
  println(p1.isNotSimilar(p4))
}
class caseclass {

}

abstract class Notification
case class Email(sourceEmail: String, title: String, body: String) extends Notification
case class SMS(sourceNumber: String, message: String) extends Notification
case class VoiceRecording(contactName: String, link: String) extends Notification

class Point(var x: Int, var y: Int) {
  def move(dx: Int, dy: Int): Unit = {
    x = x + dx
    y = y + dy
  }
  override def toString: String =
    "(" + x + ", " + y + ")"
}
object Classes {
  def main(args: Array[String]) {
    val pt = new Point(1, 2)
    println(pt)
    pt.move(10, 10)
    println(pt)
  }
}

object caseclass {
  def main(args: Array[String]): Unit = {
    val emailFromJohn = Email("john.doe@mail.com", "Greetings From John!", "Hello World!")
    val title = emailFromJohn.title
    println(title) // prints "Greetings From John!"

    //emailFromJohn.title = "Goodbye From John!" // This is a compilation error. We cannot assign another value to val fields, which all case classes fields are by default.

    val editedEmail = emailFromJohn.copy(title = "I am learning Scala!", body = "It's so cool!")
    println(emailFromJohn) // prints "Email(john.doe@mail.com,Greetings From John!,Hello World!)"
    println(editedEmail) // prints "Email(john.doe@mail.com,I am learning Scala,It's so cool!)"


    val firstSms = SMS("12345", "Hello!")
    val secondSms = SMS("12345", "Hello!")
    if (firstSms == secondSms) {
      println("They are equal!")
    }
    println("SMS is: " + firstSms)

    def showNotification(notification: Notification): String = {
      notification match {
        case Email(email, title, _) =>
          "You got an email from " + email + " with title: " + title
        case SMS(number, message) =>
          "You got an SMS from " + number + "! Message: " + message
        case VoiceRecording(name, link) =>
          "you received a Voice Recording from " + name + "! Click the link to hear it: " + link
      }
    }

//    val someSms = SMS("12345", "Are you there?")
//    val someVoiceRecording = VoiceRecording("Tom", "voicerecording.org/id/123")
//    println(showNotification(someSms))
//    println(showNotification(someVoiceRecording))
    // prints:
    // You got an SMS from 12345! Message: Are you there?
    // you received a Voice Recording from Tom! Click the link to hear it: voicerecording.org/id/123

    def showNotificationSpecial(notification: Notification, specialEmail: String, specialNumber: String): String = {
      notification match {
        case Email(email, _, _) if email == specialEmail =>
          "You got an email from special someone!"
        case SMS(number, _) if number == specialNumber =>
          "You got an SMS from special someone!"
        case other =>
          showNotification(other) // nothing special, delegate to our original showNotification function
      }
    }

    val SPECIAL_NUMBER = "55555"
    val SPECIAL_EMAIL = "jane@mail.com"
    val someSms = SMS("12345", "Are you there?")
    val someVoiceRecording = VoiceRecording("Tom", "voicerecording.org/id/123")
    val specialEmail = Email("jane@mail.com", "Drinks tonight?", "I'm free after 5!")
    val specialSms = SMS("55555", "I'm here! Where are you?")
    println(showNotificationSpecial(someSms, SPECIAL_EMAIL, SPECIAL_NUMBER))
    println(showNotificationSpecial(someVoiceRecording, SPECIAL_EMAIL, SPECIAL_NUMBER))
    println(showNotificationSpecial(specialEmail, SPECIAL_EMAIL, SPECIAL_NUMBER))
    println(showNotificationSpecial(specialSms, SPECIAL_EMAIL, SPECIAL_NUMBER))
    // prints:
    // You got an SMS from 12345! Message: Are you there?
    // you received a Voice Recording from Tom! Click the link to hear it: voicerecording.org/id/123
    // You got an email from special someone!
    // You got an SMS from special someone!

    object MatchTest1 extends App {
      def matchTest(x: Int): String = x match {
        case 1 => "one"
        case 2 => "two"
        case _ => "many"
      }
      println(matchTest(3))
    }

  }
}

abstract class AbsIterator {
  type T
  def hasNext: Boolean
  def next: T
}

trait RichIterator extends AbsIterator {
  def foreach(f: T => Unit) { while (hasNext) f(next) }
}

class StringIterator(s: String) extends AbsIterator {
  type T = Char
  private var i = 0
  def hasNext = i < s.length()
  def next = { val ch = s charAt i; i += 1; ch }
}

object StringIteratorTest {
  def main(args: Array[String]) {
    class Iter extends StringIterator("I am all right!!") with RichIterator
    val iter = new Iter
    iter foreach print
  }
}

class Graph {
  class Node {
    var connectedNodes: List[Node] = Nil
    def connectTo(node: Node) {
      if (connectedNodes.find(node.equals).isEmpty) {
        connectedNodes = node :: connectedNodes
      }
    }
  }
  var nodes: List[Node] = Nil
  def newNode: Node = {
    val res = new Node
    nodes = res :: nodes
    res
  }
}

object GraphTest extends App {
  val g = new Graph
  val n1 = g.newNode
  val n2 = g.newNode
  val n3 = g.newNode
  n1.connectTo(n2)
  n3.connectTo(n1)
}


class TGraph {
  class Node {
    var connectedNodes: List[Graph#Node] = Nil
    def connectTo(node: Graph#Node) {
      if (connectedNodes.find(node.equals).isEmpty) {
        connectedNodes = node :: connectedNodes
      }
    }
  }
  var nodes: List[Node] = Nil
  def newNode: Node = {
    val res = new Node
    nodes = res :: nodes
    res
  }
}