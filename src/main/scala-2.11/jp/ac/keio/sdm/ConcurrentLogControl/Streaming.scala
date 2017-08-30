package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.streaming.{StreamingContext, Duration}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

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
    val hashTagStream = twitterStream.filter(_.getLang == "en").map(_.getText).flatMap(_.split(" ")).filter(_.startsWith("#"))

    // Compute the counts of each hashtag by window.
    val windowedhashTagCountStream = hashTagStream.map((_, 1)).reduceByKeyAndWindow((x: Int, y: Int) => x + y, windowLength, slideInterval)

    windowedhashTagCountStream.saveAsTextFiles("output/tweet")

    ssc
  }

  def doStreaming(args: Array[String], ssc: StreamingContext) {

    // Create a DStream that returns tweets received from Twitter.
    val twitterStream = TwitterUtils.createStream(ssc, None)

    // Create a Log Filter that periodically output logs from a Log Cache.
    val logFilter = new LogFilter
    logFilter.executeFilter()

    twitterStream

      .flatMap { status =>
        val text = status.getText()
        // Create a Analyzer
        val analyzer = new JapaneseAnalyzer
        // Returns a TokenStream suitable for fieldName, tokenizing the contents of reader
        val tokenStream = analyzer.tokenStream("", text)
        // This method first checks if an instance of that class is already in this AttributeSource and returns it
        // Otherwise a new instance is created, added to this AttributeSource and returned
        val charAttr = tokenStream.addAttribute(classOf[CharTermAttribute])
        // This method is called by a consumer before it begins consumption using incrementToken()
        tokenStream.reset()

        try {
          Iterator
            // Creates an infinite-length iterator returning the results of evaluating an expression.
            // Advance the stream to the next token
            .continually(tokenStream.incrementToken())
            // An iterator returning elements from it as long as condition p is true
            .takeWhile(identity)
            .map(_ => charAttr.toString)
            // Convert this vector to Vector
            .toVector
        } finally {
          tokenStream.end()
          tokenStream.close()
        }

      }

      .flatMap(_.split(" "))
      /*.map(s => {
        try {
          s(10000)
        } catch {
          case runtime : RuntimeException => {
            logger.warn("EXP-E000001", runtime)
          }
        }
      })*/
      .map(s => {
      try {
        s(10000)
      } catch {
        case runtime : RuntimeException => {
          LogCache.putIfAbsent("EXP-E000001", runtime)
        }
      }
      })
      .map(word => (word, 1))
      .reduceByKey((a, b) => a + b)
      .saveAsTextFiles("output/tweet")
      // Save to the Amazon S3 bucket
      // .saveAsTextFiles("s3://aws-logs-757020086170-us-west-2/output/tweet")
  }
}