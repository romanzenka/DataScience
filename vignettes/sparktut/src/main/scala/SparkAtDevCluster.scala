import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object SparkAtDevCluster {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("spark://MachineLearningSparkCluster.azurehdinsight.net:7077").setAppName("My App")
    val sc = new SparkContext(conf)

    println("Finished")
  }
}
