package spark.mltest

import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by I311352 on 3/29/2017.
  */
class mltest {

}

object mltest extends App {
  val conf = new SparkConf().setAppName("mltest").setMaster("local[2]")
  val sc = new SparkContext(conf)
  val sQLContext = new SQLContext(sc)
  println("OK")

  val training = sc.parallelize(Seq(
    LabeledPoint
  ))
}