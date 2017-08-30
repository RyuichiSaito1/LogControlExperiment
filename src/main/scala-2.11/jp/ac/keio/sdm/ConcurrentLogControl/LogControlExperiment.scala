package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, StreamingContext}

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment extends LogControlExperimentFigure {

  val ThreadCount = 4
  val ApplicationName = "LogControlExperiment"
  // Recompute the top hashtags every 1 second
  val SlideInterval = new Duration(1 * 1000)
  // Define the number of threads.
  val SparkUrl = "local[" + ThreadCount + "]"

  def main(args: Array[String]) {

    // Configure Spark Properties.
    val conf = new SparkConf().setMaster(SparkUrl).setAppName(ApplicationName)
    val ssc = new StreamingContext(conf, SlideInterval)

    // Create a Log Filter that periodically output logs from a Log Cache.
    val logFilter = new LogFilter
    logFilter.executeFilter()
    // Execute Spark Streaming.
    Streaming.createTweetsWordCount(ssc, SlideInterval)

    ssc.start()
    ssc.awaitTermination()
    logFilter.shutdownScheduledThreadPoolExecutor()
  }
}