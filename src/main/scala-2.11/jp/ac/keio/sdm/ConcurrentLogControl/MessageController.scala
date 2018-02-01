/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.Properties

/**
  * Created by Ryuichi on 4/3/2017 AD.
  */
object MessageController {

  val properties = new Properties()

  def initialize(): Unit = {
    properties.load(getClass.getResourceAsStream("/message.properties"))
  }

  def getMessage(messageID: String): String = {
    val messageBody = properties.getProperty(messageID)
    messageBody
  }
}
