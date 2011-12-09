name := "countdown"

version := "1.0"

organization := "za.co.countdown"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "joda-time" % "joda-time" % "1.6.2",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.2.0",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.2.0",
  "net.databinder" %% "unfiltered-filter" % "0.5.0",
  "net.databinder" %% "unfiltered-jetty" % "0.5.0",
  "net.liftweb" %% "lift-json" % "2.4-M4",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
  "org.scalaz" %% "scalaz-core" % "6.0.3",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.9")
