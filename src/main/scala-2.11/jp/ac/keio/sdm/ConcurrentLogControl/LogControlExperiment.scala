package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment extends LogControlExperimentFigure {

  val ThreadCount = 4
  val ApplicationName = "LogControlExperiment"
  val BatchDuration = 1
  // Define the number of threads.
  val SparkUrl = "local[" + ThreadCount + "]"

  def main(args: Array[String]) {

    // Configure Spark Properties.
    val conf = new SparkConf().setMaster(SparkUrl).setAppName(ApplicationName)
    val ssc = new StreamingContext(conf, Seconds(BatchDuration))

    // Execute Spark Streaming.
    Streaming.doStreaming(args, ssc)

    ssc.start()
    ssc.awaitTermination()
  }
}