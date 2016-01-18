import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import scala.reflect.io.File
import scala.runtime.ScalaRunTime._
import java.io._

/**
 * Created by m102417 on 10/9/15.
 * This is a collection of examples using the SparkAPI
 *
 * great training on spark is here:
 * https://www.youtube.com/watch?list=PLTPXxbhUt-YWSgAUhrnkyphnh0oKIT8-j&v=VWeWViFCzzg
 *
 */
object RDDapi {


  //common transformations: (lazy-evaluations)
  //map(func) : returns a new distributed dataset by passing each element of the source through a function, func
  //filter(func): returns a new dataset formed by selecting those elements of the source on which func returns true
  //flatMap(func): similar to map, but each input item can be mapped to 0 or more output items(so func should return a Seq rather than a single item)
  //sample(withReplacement, fraction, seed): sample a fraction, fraction, of the data with or without replacement using a given random number generator, seed
  //union(otherDataset) return a new dataset that contains the union of the elements in the source dataset and the argument
  //distinct([numTasks]) return a new dataset that contains the distinct elements of the source dataset.
  //groupByKey([numTasks]) when called on a dataset of (K,V) pairs, return a dataset of (K, Seq[V]) pairs
  //reduceByKey(func,[numTasks]) when called on a dataset of (K,V) pairs, returns a dataset of (K,V) pairs where the values for each key are aggregated using the given reduce function
  //sortByKey([acending],[numTasks]) when called on a dataset of (K,V) pairs where K implements Ordered, returns a dataset of (K,V) pairs sorted by keys in accending or decending order, as specified in the boolean accending argument.
  //join(otherDataset, [numTasks]) when called on datasets of type (K,V) and (K,W), returns a dataset of (K,(V,W)) pairs with all pairs of elements for each key
  //cogroup(otherDataset, [numTasks]) when called on datasets of type (K,V) and (K,W), returns a dataset of (K, Seq[V], Seq[W]) touples - also called groupWith
  //cartesian(otherDataset) when called on datasets of types T and U, returns a dataset of (T,U) pairs (all pairs of elements)

  //common actions
  //reduce(func) - aggregate the elements of the dataset using a function, func, (which takes two arguments and returns one), and should also be communative and associative so that it can be computed correctly in parallel
  //collect() returns all the elements of the dataset as an array at the driver program - usually only useful after filter or other operations have reduced the dataset to a sufficently small subset
  //count() return the number of elements in the dataset
  //first() return the first element of the dataset- similar to take(1)
  //takeSample(withReplacement, fraction, seed) return an array with a random sample of num elements of the dataset, with or without repacement using the given random number generator and seed.
  //saveAsTextFile(path)
  //saveAsSequenceFile(path) - saves to HDFS as a sequence of flat files
  //countByKey() - use only on RDDs of type (K,V), returns a map of (K,Int) pairs with the count of each key
  //foreach(func) - run a function, func on each element of the dataset, usually done for side-effect actions such as updating an accumulator variable or interacting with external storage systems.

  //persistance:
  //MEMORY_ONLY, MEMORY_AND_DISK, MEMORY_ONLY_SER, MEMORY_AND_DISK_SER, DISK_ONLY, MEMORY_ONLY_2, ...


  def main(args: Array[String]) {
    // local - run Spark locally with one worker thread (no-parallel)
    // local[K] - run Spark locally with K worker threads, ideally set to the number of cores
    // spark://HOST:PORT connect to a Spark standalone cluster: PORT depends on config (7077 by default)
    // mesos://HOST:PORT connects to a Mesos cluster: PORT depends on config (5050 by default)
    val conf = new SparkConf().setMaster("local").setAppName("My App")
    val sc = new SparkContext(conf)
    createData(sc);
    countCHR(sc);
    wordCount(sc);
    flatMap(sc);
    join(sc);
    println("Finished")
  }

  //1
  def createData(sc : SparkContext){
    val data = 1 to 10000;
    val distData = sc.parallelize(data); //convert the variable to an RDD
    //RDD _transformations are lazy evaluated; so nothing happens until the _action:'filter'
    val result = distData.filter(_ < 10).collect();  //bring the data back and execute the exe graph (thankfully data is small)
    print(result.getClass().getSimpleName() + "\n"); //int[]
    println(stringOf(result));  //Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
  }

  //2 - counts the number of each chrom in a VCF
  def countCHR(sc: SparkContext){
    val lines = sc.textFile("src/test/resources/example.vcf"); //can use "hdfs://..." as a path
    val notheader = lines.filter(!_.startsWith("#"));  //we don't want header lines
    val a = notheader.collect();
    //sanity check to print all the lines to the screen
    for (line <- a){
      println(line)
    }
    //x.split("\t")(0),1) takes a vcf line and makes a touple like this: (22,1)
    //reduceByKey counts the instances of each touple
    val chrCount = notheader.map(x => (x.split("\t")(0),1)).reduceByKey((x, y) => x + y);
    val b = chrCount.collect();
    for( line <- b){
      println(line)
    }
    //result should be:
    //(21,36)
    //(22,136)
    //(1,1)
  }

  //3
  def wordCount(sc:SparkContext) {
    val lines = sc.textFile("src/test/resources/AliceInWonderland.txt");
    //flatMap one-to-many fan out from lines to words
    //map one-to-one mapping from words to touples
    //reduceBy - count up the results
    val wc = lines.flatMap(l => l.split(" ")).map(word => (word,1)).reduceByKey(_ + _);
    val outfile = "/tmp/AliceCounts.txt";
    val f = new java.io.File(outfile);
    if(f.exists){ delete(f); }
    //action that creates the output!
    wc.saveAsTextFile(outfile)
    //note compatibility between python and java/scala becomes a problem... probably need a common encoding format e.g. avro,parquet
    println("Saving Word Count Results to: " + outfile)
  }

  def delete(file: java.io.File): Array[(String, Boolean)] = {
    Option(file.listFiles).map(_.flatMap(f => delete(f))).getOrElse(Array()) :+ (file.getPath -> file.delete)
  }

  //4
  def flatMap(sc:SparkContext){
    println("MAP VS FLATMAP!")
    println("MAP:")
    //this shows the difference between a map (above) and a flatmap
    val lines = sc.textFile("src/test/resources/AliceInWonderland.txt");
    val map = lines.map(l => l.split(" ")).take(10); //only 1st 10, this is big
    for(line <- map){ println(stringOf(line)); }
    //Array(Project, Gutenberg's, Alice's, Adventures, in, Wonderland,, by, Lewis, Carroll)
    //Array("")
    //Array(This, eBook, is, for, the, use, of, anyone, anywhere, at, no, cost, and, with)
    //Array(almost, no, restrictions, whatsoever., "", You, may, copy, it,, give, it, away, or)
    //Array(re-use, it, under, the, terms, of, the, Project, Gutenberg, License, included)
    //Array(with, this, eBook, or, online, at, www.gutenberg.org)
    //Array("")
    //Array("")
    //Array(Title:, Alice's, Adventures, in, Wonderland)
    //Array("")
    println("FLATMAP:")
    val flatmap = lines.flatMap(l => l.split(" ")).take(10);
    for(line <- flatmap){ println(stringOf(line)); }
    //Project
    //Gutenberg's
    //Alice's
    //Adventures
    //in
    //Wonderland,
    //by
    //Lewis
    //Carroll
    //""
  }

  //5
  def join(sc:SparkContext){
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

    case class Register (d: java.util.Date, uuid: String, cust_id: String, lat: Float, lng: Float)
    case class Click (d: java.util.Date, uuid: String, landing_page: Int)

    val reg = sc.textFile("src/test/resources/reg.tsv").map(_.split("\t")).map(
      r => (r(1), Register(format.parse(r(0)), r(1), r(2), r(3).toFloat, r(4).toFloat))
    )
    val a = reg.collect();
    for(line <- a){
      println(line)
    }
    println("*******")
    val clk = sc.textFile("src/test/resources/clk.tsv").map(_.split("\t")).map(
      c => (c(1), Click(format.parse(c(0)), c(1), c(2).trim.toInt))
    )
    val b = clk.collect();
    for(line <- b){
      println(line)
    }

    val r = reg.join(clk).take(2) //join will join based on the keys, take(n) will get the nth element
    println(stringOf(r));

    println("***********");
    //use the following to debug
    println(reg.join(clk).toDebugString)
  }

  //6



}
