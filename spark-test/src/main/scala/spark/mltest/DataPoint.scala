package spark.mltest

import breeze.linalg.DenseVector

/**
  * Created by I311352 on 4/6/2017.
  */
case class DataPoint(x: DenseVector[Double], y: Double)

object DataPoint {
  def generateData(numData: Int) = {
    (1 to numData).map(value => {
      val x1 = value * 1.0
      val x2 = value * 10 * 1.0
      val y = 0 + 2.5 * x1 + 1.5 * x2
      val valueVec = new DenseVector(Array(x1, x2))
      DataPoint(valueVec, y)
    })
  }
}

