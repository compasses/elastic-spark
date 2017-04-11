package spark

import org.apache.spark.SparkContext

/**
  * Created by I311352 on 4/11/2017.
  */
object basicTest  extends App {
  val master = args.length match {
    case x: Int if x > 0 => args(0)
    case _ => "local"
  }

  val sc = new SparkContext(master, "PerKeyAvg", System.getenv("SPARK_HOME"))
  val input = sc.parallelize(List(("coffee", 1) , ("coffee", 2) , ("panda", 4)))

  val result = input.combineByKey(
    (v) => (v, 1),
    (acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),
    (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)
    // Note: we could us mapValues here, but we didn't because it was in the next section
  ).map{ case (key, value) => (key, value._1 / value._2.toFloat) }

  result.collectAsMap().map(println(_))

  val data = Seq(("a", 3), ("b", 4), ("a", 1))
  val re2 = sc.parallelize(data).reduceByKey((x, y) => x + y) // Default parallelism
  re2.collect().map(println(_))
  println(re2.getNumPartitions)

  sc.parallelize(data).reduceByKey((x, y) => x + y)

}
