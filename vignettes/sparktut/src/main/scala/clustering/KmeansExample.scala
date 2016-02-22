package clustering


import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors

import scala.reflect.io.File

/**
 * Created by m102417 on 2/17/16.
 */
object KmeansExample {



  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("My App")
    val sc = new SparkContext(conf)

    val data = sc.textFile("src/test/resources/Iris.txt")  //the classic Iris dataset for machine learning
    //split off the classification in the third row
    val noFlowerNames = data.map(x => x.replace(",Iris-setosa","").replace(",Iris-versicolor","").replace(",Iris-virginica",""))
    //print data to screen
    for(line <- noFlowerNames.collect()){
      println(line);
    }
    val parsedData = noFlowerNames.map(s => Vectors.dense(s.split(',').map(_.toDouble))).cache()
    println(parsedData.first())

    //k-means needs a number of clusters
    val numClusters = 3 //we know this is true from the data
    val numIterations = 20

    //do the clustering
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    // Save and load model
    val outfile = "/tmp/iris.clusters"
    FileUtils.deleteQuietly(new java.io.File(outfile))
    clusters.save(sc, outfile)
    val sameModel = KMeansModel.load(sc, outfile)

    //now print out the clusters...
    val vectors = parsedData.collect();
    for(vector <- vectors){
      System.out.println("cluster "+sameModel.predict(vector) +" "+vector.toString());
    }


    println("Finished")
  }

}
