/* SampleApp.scala:
   This application simply counts the number of lines that contain "val" from itself
 */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
 
object SampleApp {
  def main(args: Array[String]) {
    val txtFile = "hdfs://localhost:9000/input/people.json"
    val conf = new SparkConf().setAppName("Sample Application").setMaster("local")
    val sc = new SparkContext(conf)
    val txtFileLines = sc.textFile(txtFile , 2).cache()
    val numAs = txtFileLines .filter(line => line.contains("val")).count()
    println("Lines with val: %s".format(numAs))

    val data = Array(1, 2, 3, 4, 5)
    val distData = sc.parallelize(data)
    val sum = distData.map(x => x * 2).sum()
    println(sum)  // 30.0

    val sum2 = distData.map(new Operation().multiply).sum()
    println(sum2)  // 30.0

  }
}
