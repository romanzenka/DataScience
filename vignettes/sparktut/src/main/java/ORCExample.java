
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import java.util.List;

/**
 * Created by m102417 on 1/19/16.
 *
 * This is an example of using ORC files in Spark
 * https://orc.apache.org/talks/
 * http://hortonworks.com/blog/bringing-orc-support-into-apache-spark/
 * http://hortonworks.com/blog/orcfile-in-hdp-2-better-compression-better-performance/
 *
 */
public class ORCExample {

    private class Contact {
        private String name;
        private String phone;
    }

    private class Person {
        private String name;
        private Integer age;
        private List<Contact> contacts;
    }

    public static void main(String[] args){

        //make a connection on localhost to spark
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My App");
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);


        System.out.println("Finished");
    }


}
