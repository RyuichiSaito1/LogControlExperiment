package jp.ac.keio.sdm

import scala.collection.JavaConversions._
import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object SimpleApp {

  def main(args: Array[String]): Unit = {

    /** Load Log4j properties and Set log level */
    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("log4j.properties"))
    PropertyConfigurator.configure(props)
    val log = LogManager.getRootLogger()
    log.setLevel(Level.WARN)

    /** Create a Spark Streaming */
    val conf = new SparkConf().setMaster("local[*]").setAppName("Simple Application")
    val ssc = new StreamingContext(conf, Durations.minutes(1L))

    /** Create a input stream that returns tweets received from Twitter */
    val filter = if (args.isEmpty) Nil else args.toList
    val stream = TwitterUtils.createStream(ssc, None, filter)

    /** Create a Log Cache */
    val lc = new LogCache()

    /** Create a Log Filter that periodically output logs from a Log Cache */
    val lf = new LogFilter(lc)

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
          lc.putIfAbsent(Thread.currentThread().getId + "MessageId", runtime.toString())
          // Need "import scala.collection.JavaConversions._" to convert Java API to Scala API.
          lc.foreach(kv => println(kv._1 + " -> " + kv._2))
        }
      }
      })
      .map(word => (word, 1))
      .reduceByKey((a, b) => a + b)
      .saveAsTextFiles("output/tweet")

    ssc.start()
    log.warn("Spark Streaming Start")
    ssc.awaitTermination()
  }
}