import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors

import java.io.File
import java.nio.file.{Paths, Files}

object ClusterObject{

  def main(args: Array[String]){
    //Checking if kmeans.xml data file exists, if it doesn't, it creates a new file to export the cluster model
    if(Files.exists(Paths.get("./data/kmeans.xml"))) {
      val file = new File("./data/kmeans.xml")
    }
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("SimpleRecommendation")
      .set("spark.executor.memory", "2g")

    val sc = new SparkContext(conf)
    // Load and parse the data
    val data = sc.textFile("data/kmeans_data.txt")
    val parsedData = data.map(s => Vectors.dense(s.split('\t').map(_.toDouble))).cache()

    // Cluster the data into k classes using KMeans
    val numClusters = 10
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)
    // Save and load model
    clusters.save(sc, "myModelPath")
    val sameModel = KMeansModel.load(sc, "myModelPath")
    sameModel.toPMML("./data/kmeans.xml")
    //sc.stop()
  }
}