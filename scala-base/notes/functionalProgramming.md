## 读书：Functional Programming In Scala （Part 1）

学习Scala更多的是一种思想的转变。不同于传统的编程语言，例如C++，Java，Golang等，个人感觉他们没有本质的区别，无非是内存、CPU、IO等等，或者他们都是命令式的编程思想，面向冯诺依曼体系结构的。无非就是内存、变量、赋值、表达式、控制结构等。所以这些语言，使用起来没有太多违和感，特别是有了较多的C++开发经验的时候。

Scala不同，它不再是那种冯诺依曼体系结构的编程思想了，更多的是一种数学的编程思想的体现。通过这本书：[Functional Programming in Scala](https://book.douban.com/subject/20488750/)，可以很好的了解到这种思想的转变。所以要比较好的掌握Scala，思想转变是必须的。那么好好看看这本书也是理所当然的了。

[Functional Programming](https://en.wikipedia.org/wiki/Functional_programming)是个很大的topic，它的理论基础是[Lambda calculus](https://en.wikipedia.org/wiki/Lambda_calculus)，Lambda calculus是一种数学计算抽象。跟上面所说那样，Lambda calculus不会关注内存、变量、赋值等语句了，而更多的是各种函数算子的变换。研究生还上过这个计算理论课，可惜当时没有学习一门函数式编程语言。所以函数式编程可以理解为面向函数的编程。就像Java一样，面向对象的编程。函数式编程的理论基础便是Lambda calculus，[这里](https://github.com/txyyss/Lambda-Calculus/releases)有对其比较清晰的介绍。

举几个例子:

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
很明显这个函数就是求取这个list的sum。通过一个函数的组合调用就能完成。如果换成其他语言来完成这个运算，可能想到的就是通过循环，定义sum变量，进行累加了。

一个复杂的例子：
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

Java8中也有Optional和Stream，但是相信大部分的程序员只是知道怎么用。例如Optional可以省掉很多null值的判断，让返回值达到统一。而Stream是一种链式流处理，也是为了迎合函数式编程。但是通过了解Scala中的Stream和Optional也许才能真正知道它们的强大之处。Java8应该是借鉴了Scala的实现，例如Lambda函数支持，虽然确实带来了很多便利，但是思想上没有改变。

Scala中的Optional是一种函数式编程中的错误处理方式，函数式编程中异常的处理不方便，也不适合。Optional可以让异常做到无感知。只是封装起来，异常发生了就会得到返一个空值而不是抛出异常。
Stream更是实现了一种lazy模式，也就是不真正使用时不会做运算处理-惰性求值。这也是函数式编程特有的方式，即声明式的编程。但声明式的编程往往会修改函数内部的某些状态，但是函数式编程是能做到[referentially transparent](https://en.wikipedia.org/wiki/Referential_transparency)的，就是所谓的纯函数式的声明式编程。书中第六章通过一个例子说明了，如何达到referentially transparent。通过把内部的状态转移暴漏出来，让开发人员决定怎么处理这样的中间状态。

函数式编程是基于不可变对象的编程，强化了面向数学编程的思想。目前只看过了这本书的第一部分，只是对函数式编程有了初步的了解，或许有些理解还不够准确，记录下先。

参考：
1. [Functional Programming in Scala](https://book.douban.com/subject/20488750/)