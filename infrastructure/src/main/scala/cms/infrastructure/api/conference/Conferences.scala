package cms.infrastructure.api.conference

import cms.domain.conference.{ConferenceCommandHandler, CreateConference}
import com.typesafe.scalalogging.Logger
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport

final class Conferences(commandHandler: ConferenceCommandHandler) extends ScalatraServlet with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  private val logger = Logger(classOf[Conferences])

  before() { contentType = formats("json") }

  post("/") {
    parsedBody.extractOpt[CreateConference] match {
      case Some(command) =>
        logger.info(s"Handling creation command from ${ request.body }")
        commandHandler.handle(command)
      case None => logger.error(s"Unable to parse creation command from ${request.body}")
    }
  }
}
