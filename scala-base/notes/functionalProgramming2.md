## 读书：Functional Programming In Scala （Part 2）

基于属性的测试：
如何定义一个正确的函数，例如一个函数能够找到一个列表中的最大值，那么这个函数至少要满足以下条件：
* The max of a single element list is equal to that element.
* The max of a list is greater than or equal to all elements of the list.
* The max of a list is an element of that list.
* The max of the empty list is unspecified and should throw an error or return `None`.

同样对一个链表求和的函数，要满足那些条件呢：
* The sum of the empty list is 0.
* The sum of a list whose elements are all equal to `x` is just the list's length multiplied by `x`. We might express this as `sum(List.fill(n)(x)) == n*x`
* For any list, `l`, `sum(l) == sum(l.reverse)`, since addition is commutative.
* Given a list, `List(x,y,z,p,q)`, `sum(List(x,y,z,p,q)) == sum(List(x,y)) + sum(List(z,p,q))`, since addition is associative. More generally, we can partition a list into two subsequences whose sum is equal to the sum of the overall list.
* The sum of 1,2,3...n is `n*(n+1)/2`.

