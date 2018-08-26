/* Copyright (c) 2017 Ryuichi Saito, Keio University. All right reserved. */
package jp.ac.keio.sdm.ConcurrentLogControl

import java.util.Properties

import org.apache.log4j.{Level, LogManager, Logger}

/**
  * Created by Ryuichi on 4/3/2017 AD.
  */
class LogControlExperimentFigure {

  val properties = new Properties()

  // Initialize Log4j.
  val logger = Logger.getLogger(this.getClass.getName)
  properties.load(getClass.getClassLoader.getResourceAsStream("log4j.properties"))
  val log = LogManager.getRootLogger()
  log.setLevel(Level.WARN)

  MessageController.initialize()
}