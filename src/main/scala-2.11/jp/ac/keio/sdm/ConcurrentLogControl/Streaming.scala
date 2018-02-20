/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Duration, StreamingContext}

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
      try {
        randomInt(21) match {
          case 1 | 4 | 7 | 10 | 13 | 16 | 19 => s(10000)
          case 2 | 5 | 8 | 11 | 14 | 17 => s.toInt
          case 3 | 6 | 9 | 12 | 15 | 18 => s.getClass()
          case 0 | 20 => s.wait()
        }
      } catch {
        case e: IllegalArgumentException => logger.error("A", e.printStackTrace())
        case e: NumberFormatException => logger.error("B", e.printStackTrace())
        case e: IllegalMonitorStateException => logger.error("C", e.printStackTrace())
      }
    })

    // Use Filtering Method.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
        .map(s => try {s(10000)} catch { case runtime : RuntimeException => { LogCache.putIfAbsent("EXP-E000001", runtime)}})*/
      // Output to file.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
      .map(s => try {s(10000)} catch { case runtime : RuntimeException => { logger.warn("EXP-E000001", runtime)}})*/

    // Compute the counts of each hashtag by window.
    val windowedhashTagCountStream = hashTagStream.map((_, 1)).reduceByKeyAndWindow((x: Int, y: Int) => x + y, windowLength, slideInterval)

    // Development Mode
    windowedhashTagCountStream.saveAsTextFiles("tweets")
    // Product Mode
    // windowedhashTagCountStream.saveAsTextFiles("s3://aws-logs-757020086170-us-west-2/elasticmapreduce/tweets")

    ssc
  }
}