## 读书：Functional Programming In Scala （Part 1）

学习Scala更多的是一种思想的转变。不同于传统的编程语言，例如C++，Java，Golang等，个人感觉他们没有本质的区别，无非是内存、CPU、IO等等。所以有三年的C++开发经验，不管是写Java还是Golang，没感觉到违和感。跳越不是很大。直到最近，想学习下Scala。看了这本书，简直让人兴奋。意识到学习Scala更多的是一种思想的转变，不是传统的面向『计算机的』开发了，而是面向函数式的。所以要比较好的掌握Scala，思想转变是必须的。

[Functional Programming](https://en.wikipedia.org/wiki/Functional_programming)是个很大的topic，它的理论基础是[Lambda calculus](https://en.wikipedia.org/wiki/Lambda_calculus)，Lambda calculus是一种数学计算抽象。研究生期间还上过这个计算理论课。理解基本上就是一些Lambda算子的各种变换和证明。所以函数式编程可以理解为面向函数的编程。就像Java一样，面向对象编程，而它的理论基础便是Lambda calculus，[这里](https://github.com/txyyss/Lambda-Calculus/releases)有对其比较好的介绍。

例如数学里面的函数二元变量的函数：f(x, y) = x\*x + y\*y，可以抽象表示成：x->(y->x\*x+y\*y)，那么一次调用(x->(y->x\*x+y\*y))(3)(4)，就如同调用f(3, 4)。所以可以把任意的多元函数转变成一元的高阶函数，这个过程也要Currying。例如代码：

```scala
  def foldLeft[A,B](as: List[A], z: B)(f: (B, A) => B): B =
    as match {
      case Nil => z
      case Cons(h, t) => foldLeft(t, f(z,h))(f)
    }
```
其中List的定义：

```scala
sealed trait List[+A]
case object Nil extends List[Nothing]
case class Cons[+A](head: A, tail: List[A]) extends List[A]
```
理解foldLeft是理解其他函数的基础。它所做的就是将链表中的元素实施一个函数变换，也可以理解为规约。比如一个例子：

```scala
  def sum(ns: List[Int]) =
    foldLeft(ns, 0)((x,y)=>x+y)
```
很明显这个函数就是求取这个list的sum。通过一个函数的组合调用就能完成。一个复杂的例子：

```scala
def reverse[A](l: List[A]): List[A] = 
foldLeft(l, List[A]())((acc,h) => Cons(h,acc))
```
reverse顾名思义，将链表反转。通过初始化的空链接表作为初始值，从原始链表的头开始构建新链表。这也是foldLeft的含义，从头开始折叠。当然最终的效果就是通过一个变换函数来达到目的。

相应的看下foldRight的定义：
```scala
  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B =
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }
```
从右边开始折叠，也就是从最后一个元素开始进行函数变换。


Stream and Optional


Java8中也有Optional和Stream，但是相信大部分的程序员只是知道怎么用。例如Optional可以省掉很多null值的判断，让返回值达到统一。而Stream是一种链式流处理，也是为了迎合函数式编程。但是通过了解Scala中的Stream和Optional也许才能真正知道它们的强大之处。

Optional是一种函数式编程中的错误处理方式，函数式编程中异常的处理不方便，也不适合。Optional可以让异常做到无感知。只是封装起来，异常发生了就返回一个空值。
Stream更是实现了一种lazy模式，也就是不真正使用时不会做运算处理。这也是函数式编程特有的方式，即声明式的编程。




