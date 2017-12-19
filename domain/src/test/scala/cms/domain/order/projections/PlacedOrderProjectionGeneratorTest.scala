package cms.domain.order.projections

import cms.domain.InMemoryProjectionRepository
import cms.domain.order.{OrderPlaced, OrderRejected, SeatsReservationConfirmed}
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class PlacedOrderProjectionGeneratorTest extends FlatSpec with Matchers with OptionValues{

  trait Setup {
    val projectionRepository = new InMemoryProjectionRepository[PlacedOrderProjection] {}
    val projectionGenerator = new PlacedOrderProjectionGenerator(projectionRepository)
  }

  "A placed order projection generator" should "create a projection on OrderPlaced" in new Setup {

    // Given
    val orderPlaced = OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3)

    // When
    projectionGenerator apply orderPlaced

    // Then
    projectionRepository.get("ID-1").value shouldEqual PlacedOrderProjection(
      conferenceId = "mix-it-18",
      id = "ID-1",
      lastUpdate = orderPlaced.creationDate,
      status = "WAITING_FOR_RESERVATION",
      requestedSeats = "Workshop" -> 3
    )
  }

  it should "update the order status on SeatsReservationConfirmed" in new Setup {

    // Given
    projectionGenerator apply OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3)
    val seatsReservationConfirmed = SeatsReservationConfirmed(orderId = "ID-1", seats = "Workshop" -> 3)

    // When
    projectionGenerator apply seatsReservationConfirmed

    // Then
    projectionRepository.get("ID-1").value shouldEqual PlacedOrderProjection(
      conferenceId = "mix-it-18",
      id = "ID-1",
      lastUpdate = seatsReservationConfirmed.creationDate,
      status = "WAITING_FOR_PAYMENT",
      requestedSeats = "Workshop" -> 3,
      reservedSeats = Some("Workshop" -> 3)
    )
  }

  it should "update the order status on OrderRejected" in new Setup {

    // Given
    projectionGenerator apply OrderPlaced(orderId = "ID-1", conferenceId = "mix-it-18", seats = "Workshop" -> 3)
    val orderRejected = OrderRejected(orderId = "ID-1", conferenceId = "mix-it-18")

    // When
    projectionGenerator apply orderRejected

    // Then
    projectionRepository.get("ID-1").value shouldEqual PlacedOrderProjection(
      conferenceId = "mix-it-18",
      id = "ID-1",
      lastUpdate = orderRejected.creationDate,
      status = "REJECTED",
      requestedSeats = "Workshop" -> 3
    )
  }
}
