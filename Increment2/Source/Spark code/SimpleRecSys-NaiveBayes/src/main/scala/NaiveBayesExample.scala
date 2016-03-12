/**
  * Created by Rakesh on 3/11/2016.
  */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
import breeze.linalg.max
import org.apache.spark.mllib.feature.Normalizer
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}
// $example on$
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
// $example off$

object NaiveBayesExample {

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("NaiveBayesExample")
      .set("spark.executor.memory", "2g")

    val sc = new SparkContext(conf)
    // $example on$
    val data = sc.textFile("data/modeldata.txt")

    //normalization
    //val mydata = MLUtils.loadLibSVMFile(sc, "data/Watch_accelerometer.txt")

    val normalizer1 = new Normalizer(p = Double.PositiveInfinity)
   // val normalizer2 = new Normalizer(p = Double.PositiveInfinity)

    // Each sample in data1 will be normalized using $L^2$ norm.
    //val data1 = mydata.map(x => (x.label, normalizer1.transform(x.features)))




    val parsedData = data.map { line =>
      val parts = line.split(',')
     // val data1 = data.map(x => (x(0), normalizer1.transform(x.(1).split(' ')))

      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    val dataf=parsedData.map(x=>(x.label,normalizer1.transform(x.features)))
    println(dataf)
    val parsedData2 = dataf.map { line =>
      val parts = line
      // val data1 = data.map(x => (x(0), normalizer1.transform(x.(1).split(' ')))

      LabeledPoint(parts._1, parts._2)
    }
    // Split data into training (60%) and test (40%).
    val splits = parsedData2.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0)
    val test = splits(1)

    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    println("Accuracy : "+accuracy)
    // Save and load model
    model.save(sc, "output/myNaiveBayesModel")
    val sameModel = NaiveBayesModel.load(sc, "output/myNaiveBayesModel")
    // $example off$


    predictionAndLabel.collect.foreach(x=>println("Predicted rating for the moves is: "+x))
  }
}

// scalastyle:on println
