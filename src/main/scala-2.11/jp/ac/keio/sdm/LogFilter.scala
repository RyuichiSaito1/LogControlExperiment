package jp.ac.keio.sdm

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
  * Created by Ryuichi on 1/6/2017 AD.
  */
class LogFilter {

  /** Execute log output from Log Cache at 60 second intervals */
  val service  = new ScheduledThreadPoolExecutor(1);
  val future = service.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {
      println("Execute Thread")
      println(LogCache.cache.size)
      LogCache.cache.foreach(kv => println(kv._1 + " -> " + kv._2))
    }
  }, 6L, 30000L, TimeUnit.MILLISECONDS);

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
