package spark.aas

import org.apache.spark.sql.functions._
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.uncommons.maths.statistics.DataSet

/**
  * Created by i311352 on 15/04/2017.
  */
object Chap3Recommand extends App {
  val conf = new SparkConf().setAppName("ChapIntro").setMaster("local")

  val spark = SparkSession.builder
    .appName("Intro").config(conf)
    .getOrCreate
  import spark.implicits._

  val base = "hdfs://localhost:9000/recommand/"

  val rawUserArtistData = spark.read.textFile(base + "user_artist_data.txt")
  val rawArtistData = spark.read.textFile(base + "artist_data.txt")
  val rawArtistAlias = spark.read.textFile(base + "artist_alias.txt")

  println("raw user artist data" + rawUserArtistData.take(10).toList)
  println("raw artist data" + rawArtistData.take(10).toList)
  println("raw artist alias data" + rawArtistAlias.take(10).toList)

  val userArtistDF = rawUserArtistData.map{ line =>
    val Array(user, artist, _*) = line.split(" ")
    (user.toInt, artist.toInt)
  }.toDF("user", "artist")

  userArtistDF.agg(min("user"), max("user"), min("artist"), max("artist")).show()

  val artistByID = buildArtistById(rawArtistData)
  val artistAlias = buildArtistAlias(rawArtistAlias)
  val (badID, goodID) = artistAlias.head
  artistByID.filter($"id" isin (badID, goodID)).show()

  def buildArtistById(rawArtistId:Dataset[String]) :DataFrame = {
    rawArtistId.flatMap { line =>
      val (id, name) = line.span(_ != '\t')
      if (name.isEmpty) {
        None
      } else {
        try {
          Some((id.toInt, name.trim))
        } catch {
          case _: NumberFormatException => None
        }
      }
    }.toDF("id", "name")
  }

  def buildArtistAlias(rawArtistAlias:Dataset[String]):Map[Int, Int] = {
    rawArtistAlias.flatMap {
      line =>
        val Array(artist, alias) = line.split('\t')
        if (artist.isEmpty) {
          None
        } else {
          Some((artist.toInt, alias.toInt))
        }
    }.collect().toMap
  }

  class Recommender(private val spark: SparkSession) {
    import spark.implicits._
    def model(rawUserArtistData: Dataset[String],
             rawArtistData: Dataset[String],
             rawArtistAlias: Dataset[String]): Unit = {
      val bArtistAlias = spark.sparkContext.broadcast(buildArtistAlias(rawArtistAlias))
      val trainData = buildCounts(rawUserArtistData, bArtistAlias).cache()

    }
    def buildCounts(
                     rawUserArtistData: Dataset[String],
                     bArtistAlias: Broadcast[Map[Int,Int]]): DataFrame = {
      rawUserArtistData.map { line =>
        val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
        val finalArtistID = bArtistAlias.value.getOrElse(artistID, artistID)
        (userID, finalArtistID, count)
      }.toDF("user", "artist", "count")
    }

//    def buildCounts(rawUserArtistData: Dataset[String], bArtistAlias: Broadcast[ Map[Int, Int]]):DataFrame = {
//      rawUserArtistData.map{
//        line =>
//        val Array(userId, artistId, count) = line.split(' ').map(_.toInt)
//          val
//      }.t
//  }
}
}
