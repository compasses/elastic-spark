package sparkstream

/**
  * Created by i311352 on 23/03/2017.
  */

import org.apache.log4j.{Level, Logger}
import org.apache.log4j.{LogManager, Level}
import org.apache.commons.logging.LogFactory

import org.apache.spark._
import org.apache.spark.streaming._

class quicktest {

}

object quicktest {
  def main(args: Array[String]): Unit = {

    Logger.getRootLogger.setLevel(Level.WARN)
    LogManager.getRootLogger().setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(conf, Seconds(1))

    // Create a DStream that will connect to hostname:port, like localhost:9999
    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    val windowedWordCounts = pairs.reduceByKeyAndWindow((a:Int,b:Int) => (a + b), Seconds(30), Seconds(10))

    wordCounts.print()

    windowedWordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }

}