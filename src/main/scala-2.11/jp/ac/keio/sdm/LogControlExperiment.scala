package jp.ac.keio.sdm

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.apache.spark.streaming.twitter.TwitterUtils

import org.apache.log4j.{Level, LogManager, PropertyConfigurator, Logger}
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment {

  def main(args: Array[String]): Unit = {

    /** Load Log4j properties */
    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("log4j.properties"))
    PropertyConfigurator.configure(props)
    val log = LogManager.getRootLogger()

    /** Set log level */
    log.setLevel(Level.WARN)
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.spark.streaming.NetworkInputTracker").setLevel(Level.INFO)

    // URL of the Spark cluster
    val sparkUrl = "local[4]"

    /** Create a Spark Streaming */
    val conf = new SparkConf().setMaster(sparkUrl).setAppName("LogControlExperiment")
    val ssc = new StreamingContext(conf, Seconds(1))

    val tweets = TwitterUtils.createStream(ssc, None)
    val statuses = tweets.map(status => status.getText())
    statuses.print()

    ssc.start()
    ssc.awaitTermination()
    /*
    /** Create a input stream that returns tweets received from Twitter */
    val filter = if (args.isEmpty) Nil else args.toList
    val stream = TwitterUtils.createStream(ssc, None, filter)

    /** Create a Log Filter that periodically output logs from a Log Cache */
    val lf = new LogFilter

    stream

      .flatMap { status =>
        val text = status.getText
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

      .flatMap(_ split " ")
      .map(s => { try {
        s(10000)
      } catch {
        case runtime : RuntimeException => {
          LogCache.putIfAbsent(Thread.currentThread().getId + "MessageId", runtime.toString())
        }
      }
      })
      .map(word => (word, 1))
      .reduceByKey((a, b) => a + b)
      .saveAsTextFiles("output/tweet")

    ssc.start()
    log.warn("Spark Streaming Start")
    ssc.awaitTermination()*/
  }
}