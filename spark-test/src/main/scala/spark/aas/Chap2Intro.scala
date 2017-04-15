package spark.aas

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._ // for lit(), first(), etc.



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
//
  val preview = spark.read.csv("hdfs://localhost:9000/linkage")
  println(preview.first())
  preview.show()

  var parsed = spark.read
    .option("header", true)
    .option("nullValue", "?")
    .option("inferSchema", "true")
    .csv("hdfs://localhost:9000/linkage")

  parsed.show()
  val schema = parsed.schema
  schema.foreach(println)

  println(parsed.count())
  parsed.cache()
  //parsed.groupBy("is_match").count().orderBy($"count".desc).show()
  parsed.createOrReplaceTempView("linkage")

  spark.sql(
    """
      select is_match, COUNT(*) cnt from linkage
      group by is_match order by cnt DESC
    """.stripMargin).show()

  val summary = parsed.describe()
  summary.show()
  summary.select("summary", "cmp_fname_c1", "cmp_fname_c2").show()

  val matches = parsed.where("is_match = true")
  val misses = parsed.filter($"is_match" === lit(false))
  val matchSummary = matches.describe()
  val missSummary = misses.describe()

  val matchSummaryT = pivotSummary(matchSummary)
  val missSummaryT = pivotSummary(missSummary)

  matchSummaryT.createOrReplaceTempView("match_desc")
  missSummaryT.createOrReplaceTempView("miss_desc")
  spark.sql("""
      SELECT a.field, a.count + b.count total, a.mean - b.mean delta
      FROM match_desc a INNER JOIN miss_desc b ON a.field = b.field
      ORDER BY delta DESC, total DESC
    """).show()



  def pivotSummary(desc: DataFrame): DataFrame = {
    val lf = longForm(desc)
    lf.groupBy("field")
      .pivot("metric", Seq("count", "mean", "stddev", "min", "max")).agg(first("value"))
  }

  def longForm(desc: DataFrame) : DataFrame = {
    import desc.sparkSession.implicits._
    val schema = desc.schema
    desc.flatMap(row => {
      val metric = row.getString(0)
      (1 until row.size).map(i => (metric, schema(i).name, row.getString(i).toDouble))
    }).toDF("metric", "field", "value")
  }

  val matchData  = parsed.as[MatchData]

  val scored = matchData.map(md=>{
    (scoreMatchData(md), md.is_match)
  }).toDF("score", "is_match")

  crossTabs(scored, 4.0).show()

  def crossTabs(scored: DataFrame, t: Double): DataFrame = {
    scored.
      selectExpr(s"score >= $t as above", "is_match").
      groupBy("above").
      pivot("is_match", Seq("true", "false")).
      count()
  }

  case class Score(value: Double) {
    def +(oi: Option[Int]) = {
      Score(value + oi.getOrElse(0))
    }
  }

  def scoreMatchData(md: MatchData): Double = {
    (Score(md.cmp_lname_c1.getOrElse(0.0)) + md.cmp_plz +
      md.cmp_by + md.cmp_bd + md.cmp_bm).value
  }

}
