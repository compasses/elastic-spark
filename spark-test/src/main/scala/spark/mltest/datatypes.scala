package spark.mltest

/**
  * Created by I311352 on 3/31/2017.
  */
import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}

class datatypes {

}

object datatypes extends App {
  val dv: Vector = Vectors.dense(1.0, 2.0, 3.0)
  val pos = LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0))
  val dm: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))

//  val observations: RDD[Vector] =  //
//
//  val summary: MultivariateStatisticalSummary = Statistics.colStats(observations)
//  println(summary.mean) // a dense vector containing the mean value for each column
//  println(summary.variance) // column-wise variance
//  println(summary.numNonzeros) // number of nonzeros in each column
}