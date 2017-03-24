package sparksql
import org.apache.spark.sql.SparkSession

/**
  * Created by i311352 on 23/03/2017.
  */
class testsql {

}

object testsql {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder().master("local[2]")
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._
  }
}
