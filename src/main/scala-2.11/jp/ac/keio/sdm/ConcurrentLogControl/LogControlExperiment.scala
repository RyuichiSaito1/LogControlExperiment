package jp.ac.keio.sdm.ConcurrentLogControl

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Ryuichi on 11/30/2016 AD.
  */
object LogControlExperiment extends LazyLogging {

  final val threadCount = 4

  def main(args: Array[String]): Unit = {

    // URL of the Spark cluster
    // Specify the number of threads
    val sparkUrl = "local[" + threadCount + "]"

    /** Create a Spark Streaming */
    val conf = new SparkConf().setMaster(sparkUrl).setAppName("LogControlExperiment")
    val ssc = new StreamingContext(conf, Seconds(1))

    // Execute streaming
    Streaming.doStreaming(args, ssc)

    ssc.start()
    // log.warn("Spark Streaming Start")
    logger.info("Spark Streaming Start")
    ssc.awaitTermination()
    }
  }