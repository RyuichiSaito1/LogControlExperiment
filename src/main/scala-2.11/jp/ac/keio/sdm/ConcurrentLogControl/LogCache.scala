package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import com.github.nscala_time.time.Imports._

/**
  * Created by Ryuichi on 1/11/2018 AD.
  */
// Check Serializable
object LogCache extends Serializable {

  final val cache = new ConcurrentHashMap[String, Object].asScala
  final val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

  def put(messageId: String, exception: Object): Option[Object] = {
    val currentDateTime = DateTime.now
    val formattedDateTime = formatter.print(currentDateTime)
    cache.put(formattedDateTime + Thread.currentThread().getId.toString + messageId + MessageController.getMessage(messageId), exception)
  }

  def putIfAbsent(messageId: String, exception: Object): Option[Object] = {
    val currentDateTime = DateTime.now
    val formattedDateTime = formatter.print(currentDateTime)
    cache.putIfAbsent(formattedDateTime + Thread.currentThread().getId.toString +  messageId + MessageController.getMessage(messageId), exception)
  }
}