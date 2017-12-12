package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Ryuichi on 4/3/2017 AD.
  */
class LogControlExperimentFigure extends LazyLogging{

  val properties = new Properties()
  // properties.load(new FileInputStream("src/main/resources/logControl.properties"))
  properties.load(getClass.getResourceAsStream("/logControl.properties"))

  MessageController.initialize()
}