name := "countdown"

version := "1.0"

organization := "za.co.countdown"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "joda-time" % "joda-time" % "1.6.2",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "com.github.scala-incubator.io" %% "scala-io-core" % "0.2.0",
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.2.0",
  "net.databinder" %% "dispatch-http" % "0.8.5",
  "net.databinder" %% "unfiltered-filter" % "0.5.0",
  "net.databinder" %% "unfiltered-jetty" % "0.5.0",
  "net.liftweb" %% "lift-json" % "2.4-M4",
  "org.specs2" %% "specs2" % "1.6.1" % "test",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
  "org.scalaz" %% "scalaz-core" % "6.0.3")

resolvers ++= Seq("dataworx" at "http://dataworx:8081/nexus/content/repositories/public")

TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -classpath "%s" %s "$@"
"""
  val mainStr = main getOrElse sys.error("No main class specified")
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "bin/run-tu.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}

