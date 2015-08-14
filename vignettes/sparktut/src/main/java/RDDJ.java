import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by m102417 on 8/14/15.
 */
public class RDDJ {

    public static void main(String[] args){

        //make a connection on localhost to spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My App");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("src/test/resources/example.vcf");
        System.out.println("Performing an action on the new RDD, actions compute a result based on an RDD, and either return it to the driver program or save it to an external storage system (e.g., HDFS).");
        String first = lines.first();
        System.out.println(first);
        assertEquals("##fileformat=VCFv4.0", first);

        System.out.println("Finished!");
    }

}
