package breeze

/**
  * Created by I311352 on 3/31/2017.
  */
import breeze.linalg._
import breeze._


object vector_test extends App {
  val vector1 = new DenseVector(Array(1.0, 2.0, 3.0))
  val vector2 = new DenseVector(Array(4.0, 5.0, 6.0))

  val res = vector1 * vector2

  println("multi result is " + res)

  val singlev = vector2.dot(vector1)

  println("Dot result is " + singlev)

  val singletoo = vector1.t * vector2

  println("Dot result is " + singletoo)

  val constant = 5.0

  vector1 :*= constant

  println("Update vector1 is " + vector1)

  val denseMatrix = DenseMatrix.eye[Double](5)
  println("original matrix" + "\n" + denseMatrix)

  // vector
  val vector3 = new DenseVector[Double](Array(1.0,2.0,4.0,5.0,1.0))
  println(denseMatrix * vector3)
  println(vector3.t * denseMatrix)

  val constantMultily = vector3 :* constant
  println("Constant multiply is " + constantMultily)

  val constantVector = DenseVector.fill(vector3.length, constant)
  println("Constant vector is " + constantVector)

  val rowVector = vector3.t
  println("row vector is " + rowVector)

  val sparseVector = new SparseVector[Double](Array(1,3,5), Array(10.0,4.0,2.0), 5)
  println(sparseVector)
}
