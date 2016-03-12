import java.net.InetAddress

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by Mayanka on 23-Jul-15.
 */
object MainStreaming {
  def main (args: Array[String]) {
    System.setProperty("hadoop.home.dir","F:\\winutils")
    val sparkConf=new SparkConf()
      .setAppName("SparkStreaming")
      .set("spark.executor.memory", "4g").setMaster("local[*]")
    val ssc= new StreamingContext(sparkConf,Seconds(2))
    val sc=ssc.sparkContext
    val ip=InetAddress.getByName("10.205.0.227").getHostName
    val lines=ssc.socketTextStream(ip,9999)
   // lines.saveAsTextFiles("output")
   val command= lines.map(x=>{
     x.toString()

    })
    command.foreachRDD(
    rdd=> rdd.collect().foreach(text => {

      println(text)
      rdd.saveAsTextFile("output")
      val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
      val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment(text)
      System.out.println("SENTIMENT is"+tweetWithSentiment)
      if(tweetWithSentiment.toString().contains("positive")){
        SimpleRecommendation.recommend(rdd.context )
      }

    })
    /*  {
          rdd.foreach(text =>{
            val sentimentAnalyzer: SentimentAnalyzer = new SentimentAnalyzer
            val tweetWithSentiment: TweetWithSentiment = sentimentAnalyzer.findSentiment(text)
            System.out.println("SENTIMENT is"+tweetWithSentiment)
          })

          //println(text)
          //print("String Format is "+rdd.toString())



       // Recommendation.recommend(rdd.context)


        /*if(rdd.collect().contains("RECOMMEND"))
        {
          Recommendation.recommend(rdd.context)
        }*/
      }*/

    )
    lines.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
