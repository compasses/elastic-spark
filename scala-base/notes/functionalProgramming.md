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






