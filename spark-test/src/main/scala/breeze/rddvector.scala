package breeze

import breeze.linalg.{DenseMatrix, DenseVector}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import spark.RecommendationExample.getClass

/**
  * Created by I311352 on 4/5/2017.
  */
object rddvector extends App {
  val LOG = LoggerFactory.getLogger(getClass)

  val conf = new SparkConf().setAppName("vector").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val data = sc.textFile("data/testdata.txt")
  println(data.take(10).toList)

  val vectorRDD = data.map(value => {
    val columns = value.split(",").map(value => value.toDouble)
    new DenseVector(columns)
  })

  println(vectorRDD.take(100).toList)

  // multiply each row by a constant vector
  val constant = 5.0
  val broadcastConstant = sc.broadcast(constant)
  val scaledRDD = vectorRDD.map(row => {
    row :* broadcastConstant.value
  })

  println(scaledRDD.take(10).toList)

  val scaledRDDByPartition = vectorRDD.glom().map((value:Array[DenseVector[Double]]) => {
    val arrayValues = value.map(denseVector => denseVector.data).flatten
    val denseMatrix = new DenseMatrix[Double](value.length,value(0).length,arrayValues)
    denseMatrix :*= broadcastConstant.value
    denseMatrix.toDenseVector
  })

  println(scaledRDDByPartition.take(10).toList)
}
