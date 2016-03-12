import java.io.File

import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source

/**
  * Created by Mayanka on 10-Mar-16.
  */
object SimpleRecommendation {

  def recommend(sc: SparkContext) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val conf = new SparkConf().setMaster("local[*]").setAppName("SimpleRecommendation")
      .set("spark.executor.memory", "2g")

    //val sc = new SparkContext(conf)

    // load personal ratings

    val myRatings = loadRatings("data/personalRating.txt")
    val myRatingsRDD = sc.parallelize(myRatings, 1)

    // load ratings and movie titles

    val movieLensHomeDir = "data"

    val ratings = sc.textFile(new File(movieLensHomeDir, "ratings.dat").toString).map { line =>
      val fields = line.split("::")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }

    val movies = sc.textFile(new File(movieLensHomeDir, "movies.dat").toString).map { line =>
      val fields = line.split("::")
      // format: (movieId, movieName)
      (fields(0).toInt, fields(1))
    }.collect().toMap

    val training=ratings.union(myRatingsRDD).repartition(4).cache()
    // Build the recommendation model using ALS
    val rank = 12
    val numIterations = 20
    val model = ALS.train(training, rank, numIterations, 0.1)

    val myRatedMovieIds = myRatings.map(_.product).toSet
    val candidates = sc.parallelize(movies.keys.filter(!myRatedMovieIds.contains(_)).toSeq)
    val recommendations = model
      .predict(candidates.map((0, _)))
      .collect()
      .sortBy(-_.rating)
      .take(30)

    var i = 1
    println("Movies recommended for you:")
    recommendations.foreach { r =>
      println("%2d".format(i) + ": " + movies(r.product))
      SocketClient.sendCommandToRobot("%2d".format(i) + ": " + movies(r.product))
      i += 1
    }

    // clean up
    sc.stop()

  }

  def loadRatings(path: String): Seq[Rating] = {
    val lines = Source.fromFile(path).getLines()
    val ratings = lines.map { line =>
      val fields = line.split("::")
      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }.filter(_.rating > 0.0)
    if (ratings.isEmpty) {
      sys.error("No ratings provided.")
    } else {
      ratings.toSeq
    }
  }

}
