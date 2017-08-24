package jp.ac.keio.sdm.ConcurrentLogControl

import java.io.FileInputStream
import java.util.Properties

/**
  * Created by Ryuichi on 4/3/2017 AD.
  */
object MessageController {

  val properties = new Properties()

  def initialize(): Unit = {
    properties.load(new FileInputStream("/Users/Ryuichi/IdeaProjects/LogControlExperiment/src/main/resources/message.properties"))
  }

  def getMessage(messageID: String): String = {
    val messageBody = properties.getProperty(messageID)
    messageBody
  }
}
