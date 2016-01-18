import com.jayway.jsonpath.JsonPath;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.io.Serializable;
import java.util.List;

/**
 * Created by m102417 on 12/8/15.
 *
 *
 * Examples from the DataFrames API pages here:
 * http://spark.apache.org/docs/latest/sql-programming-guide.html
 *
 *
 *
 */
public class DataFrames {


    public static void main(String[] args){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("Testing the Data Frames API");
        JavaSparkContext sc = new JavaSparkContext(conf);

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);


        //read an example json file into a data frame
        String geneJson = "src/test/resources/genes.first1000.json";
        DataFrame df = sqlContext.read().json(geneJson);

        // Show the content of the DataFrame
        df.show();

        // Print the schema in a tree format
        df.printSchema();

        // Select only the "HGNC" column
        df.select("HGNC").show();

        //select most everything, but in zero-based cordinates
        df.select(df.col("GeneID"), df.col("HGNC"), df.col("_landmark"), df.col("_minBP").minus(1), df.col("_maxBP") ).show();

        // Select only genes  with starts between 131124 and 142446
        df.filter(df.col("_minBP").gt(21)).show();

        // Count genes that are on the positive Strand
        df.groupBy("_strand").count().show();

        System.out.println("Distinct");
        long count = df.select(df.col("note")).distinct().count();
        System.out.println("distinct count: " + count);

        // register as a temp table inorder to use sql
        df.registerTempTable("tablename");

        // Running SQL queries programatically (run without limit if you want the entire set)
        DataFrame selectStar = df.sqlContext().sql("SELECT * from tablename").limit(10);
        for(Row r : selectStar.collect()){
            System.out.println(r.toString());
        }


        //Method to load the data into a bean
        // Load a text file and convert each line to a JavaBean.
        JavaRDD<CatalogRow> genes = sc.textFile("src/test/resources/genes.tsv.first1000").map(
                new Function<String, CatalogRow>() {
                    public CatalogRow call(String line) throws Exception {
                        String[] parts = line.split("\t");
                        CatalogRow g = new CatalogRow();
                        g.set_landmark((parts[0].trim()));
                        g.set_minBP(new Integer(parts[1].trim()));
                        g.set_maxBP(new Integer(parts[2].trim()));
                        g.setJson(parts[3].trim());
                        return g;
                    }
                });

        // Apply a schema to an RDD of JavaBeans and register it as a table.
        DataFrame schemaGenes = sqlContext.createDataFrame(genes, CatalogRow.class);
        schemaGenes.registerTempTable("genes");


        // SQL can be run over RDDs that have been registered as tables.
        System.out.println("Getting genes by queriing a large collection of objects: ");
        DataFrame gsubset = sqlContext.sql("SELECT json FROM genes WHERE _minBP >= 63885 AND _minBP <= 135677");
        for(Row r : gsubset.collect()){
            System.out.println(r.toString());
        }

        // The results of SQL queries are DataFrames and support all the normal RDD operations.
        // The columns of a row in the result can be accessed by ordinal.
        List<String> geneSubset = gsubset.javaRDD().map(new Function<Row, String>() {
            public String call(Row row) {
                String json = row.getString(0);
                String geneName = JsonPath.read(json, "GeneID");
                return "GeneID: " + geneName;
            }
        }).collect();
        for(String next : geneSubset){
            System.out.println(next);
        }


        System.out.println("Finished");

    }

    public static class CatalogRow implements Serializable {
        private String _landmark;
        private int _minBP;
        private int _maxBP;
        private String json;

        public String get_landmark() {
            return _landmark;
        }

        public void set_landmark(String _landmark) {
            this._landmark = _landmark;
        }

        public int get_minBP() {
            return _minBP;
        }

        public void set_minBP(int _minBP) {
            this._minBP = _minBP;
        }

        public int get_maxBP() {
            return _maxBP;
        }

        public void set_maxBP(int _maxBP) {
            this._maxBP = _maxBP;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }
    }
}
