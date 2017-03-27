package jp.ac.keio.sdm.ConcurrentLogControl

import java.io.FileInputStream
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}
import java.util.Properties
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Ryuichi on 1/6/2017 AD.
  */
class LogFilter extends LazyLogging {

  val properties = new Properties()
  properties.load(new FileInputStream("/Users/Ryuichi/IdeaProjects/LogControlExperiment/src/main/resources/logControl.properties"))
  /** Execute log output from Log Cache at 60 second intervals */
  val service  = new ScheduledThreadPoolExecutor(1);
  val future = service.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {
      println("Execute Thread")
      println(LogCache.cache.size)
      // LogCache.cache.foreach(kv => println(kv._1 + " -> " + kv._2))
      // LogCache.cache.foreach(kv => logger.warn(kv._1 + " -> " + kv._2.getStackTrace()))
      LogCache.cache.foreach(kv => logger.warn(kv._1 + " -> ",kv._2))
    }
  }, 6L, properties.getProperty("logFiltering.timeUnit.milliSeconds").toLong, TimeUnit.MILLISECONDS);

  var isJudgement = true

  /*while(isJudgement){
    if (future.isCancelled() || future.isDone()){
      service.shutdown()
      isJudgement = false
    }
  }*/
  if (future.isCancelled() || future.isDone()){
    service.shutdown()
    isJudgement = false
  }
}
