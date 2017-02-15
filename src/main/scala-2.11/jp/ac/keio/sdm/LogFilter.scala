package jp.ac.keio.sdm

import scala.collection.JavaConversions._
import java.util.concurrent.{ConcurrentHashMap, ScheduledThreadPoolExecutor, TimeUnit}

/**
  * Created by Ryuichi on 1/6/2017 AD.
  */
class LogFilter (logCache: LogCache){

  val service  = new ScheduledThreadPoolExecutor(1);
  val future = service.scheduleAtFixedRate(new Runnable {
    val counter = 0;
    override def run(): Unit = {
      println("Hello Thread")
      // Need "import scala.collection.JavaConversions._" to convert Java API to Scala API.
      logCache.foreach(kv => println(kv._1 + " -> " + kv._2))

      if( counter % 10 == 0 ){
        throw new RuntimeException()
      }
    }
  }, 1000L, 1000L, TimeUnit.MILLISECONDS);

  var isJudgement = true

  while(isJudgement){
    if (future.isCancelled() || future.isDone()){
      service.shutdown()
      isJudgement = false
    }
  }
}
