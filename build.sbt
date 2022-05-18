name := """gruszka"""
organization := "pro.clan"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.3"