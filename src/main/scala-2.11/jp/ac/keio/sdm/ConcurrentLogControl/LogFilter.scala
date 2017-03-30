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
      // LogCache.cache.foreach(kv => logger.warn(kv._1 + " -> ",kv._2))
      val groupedMap = LogCache.cache.groupBy(_._1 drop(23))
      // groupedMap.foreach(println(_))
      val iterator = groupedMap.iterator
      while(iterator.hasNext) {
        val(k, v) = iterator.next()
        // val groupedList = groupedMap.get(k)
        // println(k + "=" + v)
        val listSize = v.size
        // println(groupedList)
        println(listSize)
        val key = v.head
        println(key._1)
        val value = LogCache.cache.get(key._1)
        // val exception = value.asInstanceOf[RuntimeException]
        // println(value.get)
        logger.warn(key._1.toString + " -> " , value.get)
      }
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
