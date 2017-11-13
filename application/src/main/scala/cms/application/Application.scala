package cms.application

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.scalatra.servlet.ScalatraListener.LifeCycleKey

object Application extends App {

  val context = new WebAppContext

  context addServlet(classOf[DefaultServlet], "/")
  context addEventListener new ScalatraListener
  context setContextPath "/"
  context setInitParameter(LifeCycleKey, "cms.application.ScalatraBootstrap")
  context setResourceBase "src/main/webapp"

  val server = new Server(8080)
  server setHandler context

  server start()
  server join()
}
