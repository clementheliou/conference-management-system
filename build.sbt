import Dependencies._
import sbt.Keys.libraryDependencies

lazy val sharedDependencies = Seq(logback, `scala-logging`, scalatest)

lazy val sharedSettings = Seq(
  scalacOptions += "-Xfatal-warnings",
  scalaVersion := "2.12.4",
  version := "1.0-SNAPSHOT"
)

lazy val domain = project.settings(
  fork in Test := true,
  javaOptions in Test += "-Djgiven.report.dir=target/jgiven-reports/json",
  name := "domain",
  organization := "com.github.clementheliou.cms",
  sharedSettings,
  libraryDependencies ++= sharedDependencies,
  libraryDependencies ++= Seq(`jgiven-html5-report`, `jgiven-junit`, junit, `junit-interface`),
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

lazy val livingDocumentation = taskKey[Unit]("Generate HTML5 report containing business features and scenarios.")
livingDocumentation := Def.sequential(
  test in domain in Test,
  runMain in domain in Test toTask
    """ com.tngtech.jgiven.report.ReportGenerator
      |--customcss=src/test/resources/jgiven/custom.css
      |--targetDir=target/jgiven-reports/html
      |"--title=Conference Management System" """.stripMargin
).value