import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;


/**
 * Created by m102417 on 5/27/15.
 */
public class HelloSparkJ {

    public static void main(String[] args){

        //make a connection on localhost to spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My App");
        JavaSparkContext sc = new JavaSparkContext(conf);


        System.out.println("Finished");
    }
}
