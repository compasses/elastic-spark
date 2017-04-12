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

  val input2 = sc.parallelize(List("Assigns a group ID to all the jobs started by this thread until the group ID is set to a")
    .map(line=>line.split(" ").toSeq))
  println("line data" + input2.take(10).toList)

  val word2vec = new Word2Vec()
  word2vec.setMinCount(1)

  val model = word2vec.fit(input2)

  println(model.transform("to"))
  val synonyms = model.findSynonyms("a", 5)

  for((synonym, cosineSimilarity) <- synonyms) {
    println(s"$synonym $cosineSimilarity")
  }
  //model.save(sc, output)

}
