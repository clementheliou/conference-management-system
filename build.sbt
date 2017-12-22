import Dependencies._
import sbt.Keys.libraryDependencies

lazy val sharedDependencies = Seq(logback, `scala-logging`, scalatest)

lazy val sharedSettings = Seq(
  scalacOptions += "-Xfatal-warnings",
  scalaVersion := "2.12.4",
  version := "1.0-SNAPSHOT"
)

lazy val domain = project.settings(
  name := "domain",
  organization := "com.github.clementheliou.cms",
  testOptions in Test ++= Seq(
    Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
    Tests.Argument(TestFrameworks.ScalaTest, "-o")
  ),
  sharedSettings,
  libraryDependencies ++= sharedDependencies,
  libraryDependencies ++= Seq(pegdown)
)

lazy val infrastructure = project
  .dependsOn(domain)
  .settings(
    name := "infrastructure",
    organization := "com.github.clementheliou.cms",
    sharedSettings,
    libraryDependencies ++= sharedDependencies,
    libraryDependencies ++= Seq(`javax.servlet-api`, `json4s-jackson`, scalatra, `scalatra-json`)
  )

lazy val application = project
  .dependsOn(domain, infrastructure)
  .settings(
    name := "application",
    organization := "com.github.clementheliou.cms",
    sharedSettings,
    libraryDependencies ++= sharedDependencies,
    libraryDependencies ++= Seq(`jetty-server`, `jetty-servlet`, `jetty-webapp`, scalatra)
  )

lazy val root = (project in file("."))
  .aggregate(application, domain, infrastructure)
  .settings(
    name := "conference-management-system",
    organization := "com.github.clementheliou",
    sharedSettings
  )
