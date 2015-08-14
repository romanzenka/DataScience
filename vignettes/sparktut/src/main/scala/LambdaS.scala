import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._


/**
 * SPARK contains two classes of operations:
 * 1. Transformations
 * 2. Actions
 *
 * Transformations are operations on RDDs that return a new RDD, such as map() and filter(). (an RDD is a resiliant distributed dataset, see RDD.* for more details).
 * Actions are operations that return a result to the driver program or write it to storage, and kick off a computation, such as count() and first().
 * Spark treats transformations and actions very differently, so understanding which type of operation you are performing will be important.
 * If you are ever confused whether a given function is a transformation or an action, you can look at its return type:
 * transformations return RDDs, whereas actions return some other data type.
 *
 * a Lambda is a way of passing logic at runtime into a method.  It is a shorthand way to define functions inline in Scala or Python.
 * When using Spark with these languages you can also define a function separately and then pass its name to Spark for execution on the cluster
 * In general, any Transformation can be a Lambda, and some Actions can also be a Lambda.
 *
 * In Scala, we can pass in functions defined inline, references to methods, or static functions as we do for Scala’s other functional APIs.
 * Some other considerations come into play, though—namely that the function we pass and the data referenced in it needs to be serializable
 * (implementing Java’s Serializable interface). Furthermore, passing a method or field of an object includes a reference to that whole object.
 *
 * If NotSerializableException occurs in Scala, a reference to a method or field in a nonserializable class is usually the problem.
 * Note that passing in local serializable variables or functions that are members of a top-level object is always safe.
 *
 */
object LambdaS {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("My App")
    val sc = new SparkContext(conf)

    val lines = sc.textFile("src/test/resources/example.vcf") //create a RDD (resilient distributed dataset) called lines representing all the lines in the file

    //example of the simplest possible filter, defined as a lambda on the same line
    val headers = lines.filter(line => line.startsWith("#"))  //get the header lines from the vcf file.


    println("Finished")
  }
}
