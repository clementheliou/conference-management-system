package cms.infrastructure.order

import cms.domain.order.projections.PlacedOrderProjectionRepository
import cms.domain.order.{OrderCommandHandler, PlaceOrder}
import com.typesafe.scalalogging.Logger
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.JacksonJsonSupport

final class Orders(
  commandHandler: OrderCommandHandler,
  placedOrderProjectionRepository: PlacedOrderProjectionRepository
) extends ScalatraServlet with JacksonJsonSupport {

  implicit def jsonFormats: Formats = DefaultFormats

  private val logger = Logger(classOf[Orders])

  before() { contentType = formats("json") }

  get("/") {
    logger.info("Querying for the placed orders projection")
    placedOrderProjectionRepository.getAll
  }

  post("/") {
    parsedBody.extractOpt[PlaceOrder] match {
      case Some(command) =>
        logger.info(s"Handling order placing command from ${ request.body }")
        commandHandler.handle(command)
      case None => logger.error(s"Unable to parse order placing command from ${ request.body }")
    }
  }
}
