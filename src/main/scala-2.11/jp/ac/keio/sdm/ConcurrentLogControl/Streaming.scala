package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

/**
  * Created by Ryuichi on 3/23/2017 AD.
  */
object Streaming extends LogControlExperimentFigure{

  def doStreaming(args: Array[String], ssc: StreamingContext): Unit = {

    /** Create a input stream that returns tweets received from Twitter */
    val filter = if (args.isEmpty) Nil else args.toList
    val stream = TwitterUtils.createStream(ssc, None, filter)

    /** Create a Log Filter that periodically output logs from a Log Cache */
    val logFilter = new LogFilter
    logFilter.executeFilter()

    stream

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

      /*.flatMap(_.split(" "))
      .map(s => s(10000))*/
      .map(s => {
      try {
        s(10000)
      } catch {
        case runtime : RuntimeException => {
          LogCache.put("EXP-E000001", runtime)
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