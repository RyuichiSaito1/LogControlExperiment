package jp.ac.keio.sdm.ConcurrentLogControl

import com.typesafe.scalalogging.LazyLogging
import java.io.FileInputStream
import java.util.Properties

/**
  * Created by Ryuichi on 4/3/2017 AD.
  */
class LogControlExperimentFigure extends LazyLogging{

  val properties = new Properties()
  properties.load(new FileInputStream("/Users/Ryuichi/IdeaProjects/LogControlExperiment/src/main/resources/logControl.properties"))
  MessageController.initialize()

}