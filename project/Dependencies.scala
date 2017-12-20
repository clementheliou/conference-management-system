import sbt._

object Dependencies {

  val cucumberJUnit = "io.cucumber" % "cucumber-junit" % "2.3.0" % "test"
  val cucumberScala = "io.cucumber" %% "cucumber-scala" % "2.0.1" % "test"
  val javaxServletApi = "javax.servlet" % "javax.servlet-api" % "3.1.0"

  private val jettyVersion = "9.4.7.v20170914"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "compile"

  val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.5.3"
  val jUnit = "junit" % "junit" % "4.12" % "test"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val sbtJUnitInterface = "com.novocode" % "junit-interface" % "0.11" % "test" exclude("junit", "junit")
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % "test"

  private val scalatraVersion = "2.6.1"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val scalatraJson = "org.scalatra" %% "scalatra-json" % scalatraVersion
}
