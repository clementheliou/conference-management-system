import sbt._

object Dependencies {

  val `cucumber-junit` = "io.cucumber" % "cucumber-junit" % "2.3.0" % "test"
  val `cucumber-scala` = "io.cucumber" %% "cucumber-scala" % "2.0.1" % "test"
  val `javax.servlet-api` = "javax.servlet" % "javax.servlet-api" % "3.1.0"

  private val jettyVersion = "9.4.7.v20170914"
  val `jetty-server` = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val `jetty-servlet` = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val `jetty-webapp` = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "compile"

  val `json4s-jackson` = "org.json4s" %% "json4s-jackson" % "3.5.3"
  val junit = "junit" % "junit" % "4.12" % "test"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val `junit-interface` = "com.novocode" % "junit-interface" % "0.11" % "test" exclude("junit", "junit")
  val `scala-logging` = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"

  private val scalatraVersion = "2.6.1"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val `scalatra-json` = "org.scalatra" %% "scalatra-json" % scalatraVersion
}
