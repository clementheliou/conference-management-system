package cms.application

import javax.servlet.ServletContext

import cms.infrastructure.api.conference.ConferenceController
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext){
    context mount(new ConferenceController, "/api/conference/*")
  }
}
