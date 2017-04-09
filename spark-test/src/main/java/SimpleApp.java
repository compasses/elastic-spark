import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleApp {
    private static SparkConf conf = new SparkConf().setAppName("Simple Application").setMaster("local");
    private static JavaSparkContext sc = new JavaSparkContext(conf);

    private static String inputResources = "hdfs://localhost:9000/input/resources";
    private static String outputRes = "hdfs://localhost:9000/output/";

    public static void test1() {
        String logFile = "hdfs://localhost:9000/input/resources"; // Should be some file on your system
        JavaRDD<String> logData = sc.textFile(logFile).cache();
        JavaRDD<String> hdfsData  = sc.textFile("hdfs://localhost:9000/input/resources");
        LongAccumulator accum = sc.sc().longAccumulator();
        sc.parallelize(Arrays.asList(1, 2, 3, 4)).foreach(x -> accum.add(x));
        Long l = accum.value();

        hdfsData.foreach(new VoidFunction<String>() {
            public void call(String s) throws Exception {
                System.out.println( "got data " + s);
            }
        });

        int totoalLine = hdfsData.map(s->s.length()).reduce((a, b) -> a+b);
        JavaPairRDD<String, Integer> pairs = hdfsData.mapToPair(s -> new Tuple2(s, 1));
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);

        List<Integer>  data = Arrays.asList(1, 2,3,4,5,6);
        JavaRDD<Integer> distData = sc.parallelize(data);

        //pairs.saveAsTextFile("hdfs://localhost:9000/output/resources/distData2.txt");
        long numAs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("a"); }
        }).count();

        long numBs = logData.filter(new Function<String, Boolean>() {
            public Boolean call(String s) { return s.contains("b"); }
        }).count();

        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);

        sc.stop();
    }

    public static void testWordCount() {
        JavaRDD<String> textFile = sc.textFile(inputResources);
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        counts.saveAsTextFile(outputRes+"wordcount.txt");
    }

    public static void estimatePI() {
        int NUM_SAMPLES = 19000000;
        List<Integer> l = new ArrayList<>(NUM_SAMPLES);
        for (int i = 0; i < NUM_SAMPLES; i++) {
            l.add(i);
        }

        long count = sc.parallelize(l).filter(i -> {
            double x = Math.random();
            double y = Math.random();
            return x*x + y*y < 1;
        }).count();
        System.out.println("Pi is roughly " + 4.0 * count / NUM_SAMPLES);
    }

    public static void testDataFrame() {
    }

    public static void main(String[] args) {
        //testWordCount();
        estimatePI();
    }
}
