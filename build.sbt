import Dependencies._
import sbt.Keys.libraryDependencies

lazy val sharedDependencies = Seq(logback, scalaLogging, scalaTest)

lazy val sharedSettings = Seq(
  scalacOptions += "-Xfatal-warnings",
  scalaVersion := "2.12.4",
  version := "1.0-SNAPSHOT"
)

lazy val domain = project.settings(
  name := "domain",
  organization := "com.github.clementheliou.cms",
  sharedSettings,
  libraryDependencies ++= sharedDependencies,
  libraryDependencies ++= Seq(cucumberJUnit, cucumberScala, jUnit, sbtJUnitInterface)
)

lazy val infrastructure = project
  .dependsOn(domain)
  .settings(
    name := "infrastructure",
    organization := "com.github.clementheliou.cms",
    sharedSettings,
    libraryDependencies ++= sharedDependencies,
    libraryDependencies ++= Seq(javaxServletApi, json4sJackson, scalatra, scalatraJson)
  )

lazy val application = project
  .dependsOn(domain, infrastructure)
  .settings(
    name := "application",
    organization := "com.github.clementheliou.cms",
    sharedSettings,
    libraryDependencies ++= sharedDependencies,
    libraryDependencies ++= Seq(jettyServer, jettyServlet, jettyWebapp, scalatra)
  )

lazy val root = (project in file("."))
  .aggregate(application, domain, infrastructure)
  .settings(
    name := "conference-management-system",
    organization := "com.github.clementheliou",
    sharedSettings
  )
