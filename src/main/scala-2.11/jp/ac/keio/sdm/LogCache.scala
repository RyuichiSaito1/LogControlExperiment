package jp.ac.keio.sdm

import java.util.concurrent.ConcurrentHashMap
import collection.JavaConverters._

/**
  * Created by Ryuichi on 1/11/2018 AD.
  */
// Check Serializable
object LogCache extends Serializable {

  final val cache = new ConcurrentHashMap[String, String].asScala

  def putIfAbsent(key: String, value: String): Option[String] = {
    cache.putIfAbsent(key, value)
  }
}