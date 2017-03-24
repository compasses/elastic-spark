package programming

/**
  * Created by i311352 on 24/03/2017.
  */
class hello {

}


object hello extends App{
  if (args.length > 0) {
    println("hello " + args(0))
  } else {
    println("hello world")
  }
}