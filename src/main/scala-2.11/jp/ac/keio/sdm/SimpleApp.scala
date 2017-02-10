package jp.ac.keio.sdm

import scala.util._
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

    val props = new Properties()
    props.load(getClass.getClassLoader.getResourceAsStream("log4j.properties"))
    PropertyConfigurator.configure(props)
    val log = LogManager.getRootLogger()
    log.setLevel(Level.WARN)

    val conf = new SparkConf().setMaster("local[*]").setAppName("Simple Application")
    val ssc = new StreamingContext(conf, Durations.minutes(1L))
    val filter = if (args.isEmpty) Nil else args.toList
    val stream = TwitterUtils.createStream(ssc, None, filter)

    val lc = new LogCashe()
    val lf = new LogFilter(lc)

    try {
      stream
        .flatMap { status =>
          val text = status.getText

          val analyzer = new JapaneseAnalyzer
          val tokenStream = analyzer.tokenStream("", text)
          val charAttr = tokenStream.addAttribute(classOf[CharTermAttribute])

          tokenStream.reset()

          try {
            Iterator
              .continually(tokenStream.incrementToken())
              .takeWhile(identity)
              .map(_ => charAttr.toString)
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
            println("-1")
          }
        }
        })
        .map(word => (word, 1))
        .reduceByKey((a, b) => a + b)
        .saveAsTextFiles("output/tweet")
    } catch {
      case e: StringIndexOutOfBoundsException => {
        log.warn("IndexOutOfBoundsException")
      }
    }
    ssc.start()
    log.warn("Spark Streaming Start")
    ssc.awaitTermination()
  }
}