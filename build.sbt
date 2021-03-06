name := "LogControlExperiment"

version := "1.0"

scalaVersion := "2.11.8"

organization := "jp.ac.keio.sdm"

// Cached resolution is an experimental feature of sbt added since 0.13.7 to address the scalability performance of dependency resolution.
updateOptions := updateOptions.value.withCachedResolution(true)

libraryDependencies ++= Seq(
  // Must set "provided" to compile normally.
  // Development Mode.
  "org.apache.spark" %% "spark-streaming" % "2.0.1",
  // Product Mode.
  // "org.apache.spark" %% "spark-streaming" % "2.0.1" % "provided",s
  // This project forked from scala-time since it seems that scala-time is no longer maintained.
  "com.github.nscala-time" %% "nscala-time" % "2.16.0",
  // There is compatible with Spark 2.x.
  // Must match version with "spark-streaming".
  // "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.1" exclude("org.spark-project.spark", "unused"),
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.2.0" exclude("org.spark-project.spark", "unused"),
  "org.apache.lucene" % "lucene-analyzers-common" % "6.4.0",
  "org.apache.lucene" % "lucene-analyzers-kuromoji" % "6.4.0",
  // Scala Logging dependencies
  "org.slf4j" % "slf4j-api" % "1.7.25" % "compile",
  "ch.qos.logback" % "logback-classic" % "1.2.2" % "runtime",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" % "compile"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}