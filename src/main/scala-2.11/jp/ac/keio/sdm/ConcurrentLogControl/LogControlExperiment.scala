package jp.ac.keio.sdm.ConcurrentLogControl

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment extends LogControlExperimentFigure {

  final val threadCount = 4
  final val applicationName = "LogControlExperiment"
  final val batchDuration = 1

  def main(args: Array[String]): Unit = {

    // URL of the Spark cluster
    // Specify the number of threads
    val sparkUrl = "local[" + threadCount + "]"

    /** Create a Spark Streaming */
    val conf = new SparkConf().setMaster(sparkUrl).setAppName(applicationName)
    val ssc = new StreamingContext(conf, Seconds(batchDuration))

    // Execute streaming
    Streaming.doStreaming(args, ssc)

    ssc.start()
    logger.info("Spark Streaming Start")
    ssc.awaitTermination()
  }

}