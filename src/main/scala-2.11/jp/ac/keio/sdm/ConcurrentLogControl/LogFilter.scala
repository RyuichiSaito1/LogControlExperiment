package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
  * Created by Ryuichi on 1/6/2017 AD.
  */
class LogFilter extends LogControlExperimentFigure {

  val ThreadPoolSize = 1
  // 23 = HH-DD-MM_HH:MI:SS.FFF
  val DropSizeFromHead = 23
  val service  = new ScheduledThreadPoolExecutor(ThreadPoolSize);

  def executeFilter() {

    // Execute log output from Log Cache at regular intervals.
    val future = service.scheduleAtFixedRate(new Runnable {
      override def run() {

        val groupedMap = LogCache.cache.groupBy(_._1 drop(DropSizeFromHead))
        val iterator = groupedMap.iterator

        while(iterator.hasNext) {
          val(k, v) = iterator.next()
          // Get head value among List.
          val headValue = v.head
          println("Head of Value ->" + headValue._1)
          val optionValue = LogCache.cache.get(headValue._1)
          // If Option value is null, Don't output the Exception Stacktrace.
          optionValue match {
            case  Some(x) => logger.warn(headValue._1.toString + " -> " , x)
            case None => logger.warn(headValue._1.toString)
          }
        }
      }
    }, 6L, properties.getProperty("logFiltering.timeUnit.milliSeconds").toLong, TimeUnit.MILLISECONDS);
  }

  def shutdownScheduledThreadPoolExecutor() {
    service.shutdown()
  }
}