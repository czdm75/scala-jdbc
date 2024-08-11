name := "scala3-jdbc"

organization := "com.github.czdm"

version := "1.0.0-RC1"

scalaVersion := "3.2.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.13" % "test",
  "com.h2database" % "h2" % "1.4.192" % "test"
)
