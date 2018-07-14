/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, StreamingContext}

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment extends LogControlExperimentFigure {

  val ThreadCount = "*"
  val ApplicationName = "LogControlExperiment"
  // Recompute the top hashtags every 2 second
  val BatchDuration = new Duration(2 * 1000)
  val WindowLength = 16L
  val SlidingInterval = 8L
  // Define the number of threads.
  val SparkUrl = "local[" + ThreadCount + "]"

  def main(args: Array[String]) {

    // Configure Spark Properties.
    // Development Mode
    val sparkConf = new SparkConf().setMaster(SparkUrl).setAppName(ApplicationName).set("spark.task.maxFailures","100")
    // Product Mode
    // val sparkConf = new SparkConf().setAppName(ApplicationName)
    val ssc = new StreamingContext(sparkConf, BatchDuration)

    // Create a Log Filter that periodically output logs from a Log Cache.
    // val logFilter = new LogFilter
    // logFilter.executeFilter()
    // Window Length and Sliding Interval must be multiples of the batch interval of the source DStream
    Streaming.createTweetsWordCount(ssc, WindowLength, SlidingInterval)

    ssc.start()
    ssc.awaitTermination()
    // logFilter.shutdownScheduledThreadPoolExecutor()
  }
}