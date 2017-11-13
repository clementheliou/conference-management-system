package cms.infrastructure.api.conference

import org.scalatra.ScalatraServlet

class ConferenceController extends ScalatraServlet {

  get("/") {
    "Welcome to the conference controller"
  }
}
