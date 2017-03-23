package jp.ac.keio.sdm

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Seconds}

import org.apache.log4j.{Level, LogManager, PropertyConfigurator, Logger}

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
    // Specify the number of threads
    val sparkUrl = "local[4]"

    /** Create a Spark Streaming */
    val conf = new SparkConf().setMaster(sparkUrl).setAppName("LogControlExperiment")
    val ssc = new StreamingContext(conf, Seconds(1))

    // Execute streaming
    Streaming.doStreaming(args, ssc)

    ssc.start()
    log.warn("Spark Streaming Start")
    ssc.awaitTermination()
    }
  }