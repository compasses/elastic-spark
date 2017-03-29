package spark

import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * Created by I311352 on 3/29/2017.
  */
class cf {

}

object cf extends App {

}

object RecommendationExample {
  def main(args: Array[String]): Unit = {
    val LOG = LoggerFactory.getLogger(getClass)

    val conf = new SparkConf().setAppName("mltest").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val data = sc.textFile("data/test.data")
    data.foreach(r=>LOG.warn(r))
    val rating = data.map(_.split(",") match {
      case Array(user, item, rate) => Rating(user.toInt, item.toInt, rate.toDouble)
    })


    LOG.warn(rating.toString())

    // Build the recommendation model using ALS
    val rank = 10
    val numIterations = 20
    val model = ALS.train(rating, rank, numIterations, 0.01)

    val userProducts = rating map { case Rating(user, item, rating) => (user, item)}
    val predictions = model predict(userProducts) map {case Rating(user, product, rating) => ((user, product), rating)}
    val ratesAndPreds = rating.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)

    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()

    LOG.warn("Mean Squared Error = " + MSE)

  }
}