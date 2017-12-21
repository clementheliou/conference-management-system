import sbt._

object Dependencies {
  val `javax.servlet-api` = "javax.servlet" % "javax.servlet-api" % "3.1.0"

  private val jettyVersion = "9.4.7.v20170914"
  val `jetty-server` = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val `jetty-servlet` = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val `jetty-webapp` = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "compile"

  val `json4s-jackson` = "org.json4s" %% "json4s-jackson" % "3.5.3"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val `scala-logging` = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"

  private val scalatraVersion = "2.6.1"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val `scalatra-json` = "org.scalatra" %% "scalatra-json" % scalatraVersion
}
