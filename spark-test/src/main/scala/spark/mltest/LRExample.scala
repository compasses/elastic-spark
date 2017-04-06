package spark.mltest

import breeze.linalg.DenseVector
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by I311352 on 4/6/2017.
  */
object LRExample  extends App {
  val conf = new SparkConf().setAppName("Spark LRExample").setMaster("local[2]")
  val sc = new SparkContext(conf)

  val numData = 10 // Number of data points
  val D = 2 // Numer of dimensions
  val ITERATIONS = 1000

  var w = new DenseVector(Array(1.0, 1.0))
  println("Initial w: " + w)

  val data = DataPoint.generateData(numData)
  val rdd = sc.makeRDD(data)
  val learningRate = 0.00001

  for (i <- 1 to ITERATIONS) {
    println("On iterations " + i)

    val cost = sc.accumulator(0.0)
    val gradient = rdd.map(dataPoint => {
      val predictVal = (dataPoint.x.t * w)
      val diff = predictVal - dataPoint.y // h(x)- y
      val costForRow = diff * diff
      cost += costForRow
      val diffVector = new DenseVector(Array.fill[Double](D)(diff))
      val gradient = dataPoint.x *:* diffVector //   x * ( h(x) - y )
      gradient
    }).reduce((v1, v2) => v1 +:+ v2)

    val finalCost = cost.value / (numData * 2)
    println(finalCost)
    println("gradient is " + gradient)
    val learningRateVector = new DenseVector(Array.fill(D)(learningRate))
    w = w -:- (gradient *:* learningRateVector)
    println("next w is " + w)
  }

  println("Final w: " + w)
}

object LROfSpark extends App {
  val lr = new LinearRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8)
  val conf = new SparkConf().setAppName("Spark LRExample").setMaster("local[2]")
  val sc = new SparkContext(conf)


  val data = DataPoint.generateData(10)
  val rdd = sc.makeRDD(data)

 // val lrModel = lr.fit(rdd)

}
