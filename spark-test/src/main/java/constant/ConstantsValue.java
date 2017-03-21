package constant;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by i311352 on 20/03/2017.
 */
public class ConstantsValue {
    public static SparkConf conf = new SparkConf().setAppName("Simple Application").setMaster("local");
    public static JavaSparkContext sc = new JavaSparkContext(conf);

    public static String inputResources = "hdfs://localhost:9000/input/resources";
    public static String inputJson = "hdfs://localhost:9000/input/people.json";
    public static String outputRes = "hdfs://localhost:9000/output/";
}
