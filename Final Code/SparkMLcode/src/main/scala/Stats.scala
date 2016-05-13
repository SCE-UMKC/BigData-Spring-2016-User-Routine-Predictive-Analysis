import java.net.InetAddress

import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayesModel, NaiveBayes}
import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Rakesh on 5/5/2016.
  */
object Stats {
  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("Stats")
      .set("spark.executor.memory", "2g")

    //val sc = new SparkContext(conf)
    val ssc= new StreamingContext(conf,Seconds(2))
    val sc=ssc.sparkContext

    // $example on$
    val data = sc.textFile("data/Watch_accelerometer.txt")
    var count=0;

    data.foreach(line=>{
     val parts=line.split(",")
      if(parts(0).toInt==1){
        count=count+1
      }
      else if(parts(0).equals("2")){
        count=count+1
      }
    })
    println(count)
    sc.stop();
  }
}
