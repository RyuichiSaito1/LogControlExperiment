/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import scala.collection.JavaConverters._
import java.util.concurrent.ConcurrentHashMap
import com.github.nscala_time.time.Imports._

/**
  * Created by Ryuichi on 1/11/2018 AD.
  */
object LogCache extends Serializable {

  val cache = new ConcurrentHashMap[String, Object].asScala
  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

  def putIfAbsent(messageId: String, exception: Object): Option[Object] = {
    val currentDateTime = DateTime.now
    val formattedDateTime = formatter.print(currentDateTime)
    cache.putIfAbsent(formattedDateTime + " " + Thread.currentThread().getId.toString + " " + " " + messageId + MessageController.getMessage(messageId), exception)
  }

  def get(dateTime: String): Option[Object] = {
    val optionValue = cache.get(dateTime)
    optionValue
  }
}