package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Duration, StreamingContext}

import scala.math.random
import scala.math._

/**
  * Created by Ryuichi on 3/23/2017 AD.
  */
object Streaming extends LogControlExperimentFigure{

  // Compute the top hashtags for the last 5 seconds
  val windowLength = new Duration(5 * 1000)

  def createTweetsWordCount(ssc: StreamingContext, slideInterval: Duration): StreamingContext = {

    // Create a Twitter DStream for the input source.
    val twitterStream = TwitterUtils.createStream(ssc, None)

    // Parse the tweets and gather the hashTags.
    // val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" ")).filter(_.startsWith("#"))
    // Throw Exception
    val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
      .map(s =>{
      def randomInt(n: Double): Int = floor(random * n).toInt
      randomInt(11) % 2 match {
        case 0 => s(10000)
        case 1 => s.toInt
      }
    })

    // Use Filtering Method.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
      .map(s => try {s(10000)} catch { case runtime : RuntimeException => { LogCache.putIfAbsent("EXP-E000001", runtime)}})*/
    // Output file.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
      .map(s => try {s(10000)} catch { case runtime : RuntimeException => { logger.warn("EXP-E000001", runtime)}})*/

    /*val exceptionGenerator = new ExceptionGenerator
    exceptionGenerator.callException()*/

    // Compute the counts of each hashtag by window.
    val windowedhashTagCountStream = hashTagStream.map((_, 1)).reduceByKeyAndWindow((x: Int, y: Int) => x + y, windowLength, slideInterval)

    windowedhashTagCountStream.saveAsTextFiles("output/tweet")
    // Save to the Amazon S3 bucket
    // windowedhashTagCountStream.saveAsTextFiles("s3://aws-logs-757020086170-us-west-2/output/tweet")

    ssc
  }
}