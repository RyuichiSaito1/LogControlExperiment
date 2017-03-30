package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

/**
  * Created by Ryuichi on 1/11/2018 AD.
  */
// Check Serializable
object LogCache extends Serializable {

  final val cache = new ConcurrentHashMap[String, Object].asScala

  def put(key: String, value: Object): Option[Object] = {
    cache.put(key, value)
  }

  def putIfAbsent(key: String, value: Object): Option[Object] = {
    cache.putIfAbsent(key, value)
  }
}