package spark.aas

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by I311352 on 4/13/2017.
  */
case class MatchData (
    id_1: Int,
    id_2: Int,
    cmp_fname_c1: Option[Double],
    cmp_fname_c2: Option[Double],
    cmp_lname_c1: Option[Double],
    cmp_lname_c2: Option[Double],
    cmp_sex: Option[Int],
    cmp_bd: Option[Int],
    cmp_bm: Option[Int],
    cmp_by: Option[Int],
    cmp_plz: Option[Int],
    is_match: Boolean
                    )

object Chap2Intro extends App{
  val conf = new SparkConf().setAppName("ChapIntro").setMaster("local")

  val spark = SparkSession.builder
    .appName("Intro").config(conf)
    .getOrCreate
  import spark.implicits._

  val preview = spark.read.csv("hdfs://10.128.165.206:9000/linkage")
  println(preview.first())

  preview.show()
  var parsed = spark.read
    .option("header", true)
    .option("nullValue", "?")
    .option("inferSchema", "true")
    .csv("hdfs://10.128.165.206:9000/linkage")

  parsed.show()
  val schema = parsed.schema
  schema.foreach(println)

  println(parsed.count())
  parsed.cache()
  parsed.groupBy("is_match").count().orderBy($"count".desc).show()
  parsed.createOrReplaceTempView("linkage")

  spark.sql(
    """
      select is_match, COUNT(*) cnt from linkage
      group by is_match order by cnt DESC
    """.stripMargin).show()

}
