package sparktest

import org.apache.spark.util.AccumulatorV2

/**
  * Created by i311352 on 23/03/2017.
  */
class VectorAccumulatorV2  extends AccumulatorV2[MyVector, MyVector]{

  private val myVector: MyVector = MyVector.createZeroVector

  override def reset() = {
    myVector.reset()
  }

  override def add(v: MyVector) = {

  }

  override def merge(other: AccumulatorV2[MyVector, MyVector]) = {

  }

  override def copy() = {

    new VectorAccumulatorV2
  }

  override def value = {
    new MyVector
  }

  override def isZero = {
    true
  }

}
