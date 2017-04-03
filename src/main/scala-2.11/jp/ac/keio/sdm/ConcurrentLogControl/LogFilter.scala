package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
  * Created by Ryuichi on 1/6/2017 AD.
  */
class LogFilter extends LogControlExperimentFigure {

  final val threadPoolSize = 1
  final val dropSize = 23

  /** Execute log output from Log Cache at 60 second intervals */
  val service  = new ScheduledThreadPoolExecutor(threadPoolSize);
  val future = service.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {

      println("Cache size ->" + LogCache.cache.size)
      val groupedMap = LogCache.cache.groupBy(_._1 drop(dropSize))

      val iterator = groupedMap.iterator
      while(iterator.hasNext) {
        val(k, v) = iterator.next()
        // val groupedList = groupedMap.get(k)
        // Error Count
        val exceptionCount = v.size
        // println(groupedList)
        println("Number of Exception ->" + exceptionCount)
        // Get head value among the list collection
        val headValue = v.head
        println("Head of Value ->" + headValue._1)
        val optionValue = LogCache.cache.get(headValue._1)
        // If Option value is null, Don't output the Exception Stacktrace
        optionValue match {
          case  Some(x) => logger.warn(headValue._1.toString + " -> " , x)
          case None => logger.warn(headValue._1.toString)
        }
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
