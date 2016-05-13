import java.net.InetAddress

import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayesModel, NaiveBayes}
import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Rakesh on 3/11/2016.
  */
object NaiveBayesClassfy {
  def classifyAcc(userdata:String):String ={
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("NaiveBayesExample")
      .set("spark.executor.memory", "2g")
      conf.set("spark.driver.allowMultipleContexts", "true")
    //val sc = new SparkContext(conf)
    val ssc= new StreamingContext(conf,Seconds(2))
    val sc=ssc.sparkContext
/*
    // $example on$
    val data = sc.textFile("data/Watch_accelerometer.txt")

    //normalization
    //val mydata = MLUtils.loadLibSVMFile(sc, "data/Watch_accelerometer.txt")

    val normalizer1 = new Normalizer(p = Double.PositiveInfinity)
    // val normalizer2 = new Normalizer(p = Double.PositiveInfinity)

    // Each sample in data1 will be normalized using $L^2$ norm.
    //val data1 = mydata.map(x => (x.label, normalizer1.transform(x.features)))




    val parsedData = data.map { line =>
      val parts = line.split(',')
      // val data1 = data.map(x => (x(0), normalizer1.transform(x.(1).split(' ')))

      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble+20)))
    }

    /*val dataf=parsedData.map(x=>(x.label,normalizer1.transform(x.features)))
    println(dataf)
    val parsedData2 = dataf.map { line =>
      val parts = line
      // val data1 = data.map(x => (x(0), normalizer1.transform(x.(1).split(' ')))

      LabeledPoint(parts._1, parts._2)
    }*/
    // Split data into training (60%) and test (40%).
    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0)
    val test = splits(1)

    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    println("Accuracy : "+accuracy)*/
    // Save and load model
    //model.save(sc, "output/myNaiveBayesModel")
    val sameModel = NaiveBayesModel.load(sc, "output/myNaiveBayesModel")
    // $example off$

    //socket
    //socket

   /* val ip=InetAddress.getByName("10.192.0.33").getHostName
    val linesocket=ssc.socketTextStream(ip,1234)*/


/*

    val command= linesocket.map(x=>{

      val testData =Vectors.dense(x.split(',').map(_.toDouble))
      val pre=model.predict(testData)
      println(pre)
    })
*/

    // val htf = new HashingTF(10000)
    //val testData =Vectors.dense("20.0,30.0,10.0".split(',').map(_.toDouble))

    val d1=userdata.split("::")
    println(d1(0))
    val testData1 =Vectors.dense(d1(0).split(',').map(_.toDouble+20))
    println(d1)
    val pred= sameModel.predict(testData1)
    println("Hello "+pred)
    return pred+""
  }
}
