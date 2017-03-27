name := "experiment"

version := "1.0"

organization := "jp.ac.keio.sdm"

scalaVersion := "2.11.8"

// Cached resolution is an experimental feature of sbt added since 0.13.7 to address the scalability performance of dependency resolution.
updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies ++= Seq(
  // Must set "provided" to compile normally.
  // "org.apache.spark" %% "spark-streaming" % "2.0.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "2.0.1",
  // There is compatible with Spark 2.x.
  // Must match version with "spark-streaming".
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.1" exclude("org.spark-project.spark", "unused"),
  "org.apache.lucene" % "lucene-analyzers-common" % "6.4.0",
  "org.apache.lucene" % "lucene-analyzers-kuromoji" % "6.4.0",
  // Scala Logging dependencies
  "org.slf4j" % "slf4j-api" % "1.7.25" % "compile",
  "ch.qos.logback" % "logback-classic" % "1.2.2" % "runtime",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" % "compile"
)