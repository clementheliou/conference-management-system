package cms.infrastructure.conference

import cms.domain.conference._
import cms.domain.conference.projections.ConferenceProjection
import cms.domain.{CommandHandler, ProjectionRepository}
import com.typesafe.scalalogging.Logger
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport

class Conferences(
  commandHandler: CommandHandler[ConferenceCommand],
  conferenceProjectionRepository: ProjectionRepository[ConferenceProjection]
) extends ScalatraServlet with JacksonJsonSupport {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats
  private val logger = Logger(classOf[Conferences])

  before() { contentType = formats("json") }

  get("/") {
    logger.info("Querying the conferences projection")
    conferenceProjectionRepository.getAll
  }

  post("/") {
    parsedBody.extractOpt[CreateConference] match {
      case Some(command) =>
        logger.info(s"Handling creation command from ${ request.body }")
        commandHandler handle command
      case None => logger.error(s"Unable to parse creation command from ${request.body}")
    }
  }

  post("/publish") {
    parsedBody.extractOpt[PublishConference] match {
      case Some(command) =>
        logger.info(s"Handling publication command from ${ request.body }")
        commandHandler handle command
      case None => logger.error(s"Unable to parse publication command from ${ request.body }")
    }
  }

  post("/seats") {
    parsedBody.extractOpt[AddSeatsToConference] match {
      case Some(command) =>
        logger.info(s"Handling seats addition command from ${ request.body }")
        commandHandler handle command
      case None => logger.error(s"Unable to parse seats addition command from ${ request.body }")
    }
  }

  put("/") {
    parsedBody.extractOpt[UpdateConference] match {
      case Some(command) =>
        logger.info(s"Handling update command from ${request.body}")
        commandHandler handle command
      case None => logger.error(s"Unable to parse update command from ${request.body}")
    }
  }
}
