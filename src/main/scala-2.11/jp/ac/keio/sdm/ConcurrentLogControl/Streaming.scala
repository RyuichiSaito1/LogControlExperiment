/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}

/**
  * Created by Ryuichi on 3/23/2017 AD.
  */
object Streaming extends LogControlExperimentFigure{

  // Compute the top hashtags for the last 5 seconds
  val windowLength = new Duration(5 * 1000)

  /*val japaneseUnicodeBlock = new mutable.HashSet[UnicodeBlock]()
      .+=(UnicodeBlock.HIRAGANA, UnicodeBlock.HIRAGANA, UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)*/

  def createTweetsWordCount(ssc: StreamingContext, slideInterval: Duration): StreamingContext = {

    // Create a Twitter DStream for the input source.
    val twitterStream = TwitterUtils.createStream(ssc, None)

    // Parse the tweets and gather the hashTags.
    // val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" ")).filter(_.startsWith("#"))
    // Throw Exception
    val hashTagStream = twitterStream.map(_.getText).flatMap(_.split(" "))
      .map(s => {

        val validator = new Validator

        try {
          if(validator.isExistsHashTag(s)) {
            throw new HashTagException()
          }
        } catch {
          case e: Exception =>
            logger.error(MessageController.getMessage("EXP-E000001"), e)
        }

        try {
          if(validator.isJpananesUnicodeBlock(s)) {
            throw new UnicodeBlockException()
          }
        } catch {
          case e: Exception =>
            logger.error(MessageController.getMessage("EXP-E000002"), e)
        }

        try {
          if (!validator.isChackWordLengh(s)) {
            throw new NumericException()
          }
        } catch {
          case e: Exception =>
            logger.error(MessageController.getMessage("EXP-E000004"), e)
        }

        try {
          if (!validator.isChackWordLengh(s)) {
            throw new WordLengthException()
          }
        } catch {
          case e: Exception =>
            logger.error(MessageController.getMessage("EXP-E000003"), e)
        }
      })

    // Use Filtering Method.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
        .map(s => try {s(10000)} catch { case runtime : RuntimeException => { LogCache.putIfAbsent("EXP-E000001", runtime)}})*/
      // Output to file.
    /*val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" "))
      .map(s => try {s(10000)} catch { case runtime : RuntimeException => { logger.warn("EXP-E000001", runtime)}})*/

    // Compute the counts of each hashtag by window.
    // val windowedhashTagCountStream = hashTagStream.map((_, 1)).reduceByKeyAndWindow((x: Int, y: Int) => x + y, windowLength, slideInterval)
    val windowedhashTagCountStream = hashTagStream.map((_, 1)).reduceByKeyAndWindow((x: Int, y: Int) => x + y, Seconds(20), Seconds(10))
    // Development Mode
      windowedhashTagCountStream.saveAsTextFiles("tweets")
    // Product Mode
    // windowedhashTagCountStream.saveAsTextFiles("s3://aws-logs-757020086170-us-west-2/elasticmapreduce/tweets/tweets")
    ssc
  }
}