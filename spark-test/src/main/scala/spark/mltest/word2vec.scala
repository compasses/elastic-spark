package spark.mltest

import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by I311352 on 4/5/2017.
  */
object word2vec extends App {

  val text8 = "C:\\Users\\i311352\\Downloads\\text8"
  val output = "data/model"
  val conf = new SparkConf().setAppName("Spark Word2Vec").setMaster("local[2]")
  val sc = new SparkContext(conf)

  val input = sc.textFile(text8).map(line => line.split(" ").toSeq)
  println("line data" + input.take(10).toList)

  val word2vec = new Word2Vec()

  val model = word2vec.fit(input)

  val synonyms = model.findSynonyms("and", 5)

  for((synonym, cosineSimilarity) <- synonyms) {
    println(s"$synonym $cosineSimilarity")
  }
  //model.save(sc, output)

}
