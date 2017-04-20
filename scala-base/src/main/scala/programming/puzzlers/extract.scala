package programming.puzzlers

/**
  * Created by I311352 on 4/20/2017.
  */

trait A

class B (val p1:String, val p2:String) extends A

object MM{
  def apply(p1:String, p2:String) : A = new B(p1,p2);

  def unapply(a:A) : Option[(String, String)] = {
    println("on unapply...")
    if (a.isInstanceOf[B]) {
      val b = a.asInstanceOf[B]
      Some(Tuple2(b.p1, b.p2))
    }
    None
  }
}

object testdd extends App {
  val a:A = MM ("String1", "String2")
  a match {
    case b:B => println("Got b" + b.p2+b.p1)
    case _ => println("ttt")
  }
}