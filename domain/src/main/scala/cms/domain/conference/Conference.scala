package cms.domain.conference

import cms.domain.EventSourcedAggregate
import com.typesafe.scalalogging.Logger

import scala.collection.immutable.Map.empty

final class Conference private(val id: String, history: Seq[ConferenceEvent] = Nil) extends EventSourcedAggregate {

  override type EventType = ConferenceEvent

  private[this] val logger = Logger(classOf[Conference])
  protected val state = ConferenceDecisionProjection().computeFrom(history)

  private def this(name: String, slug: String){
    this(slug)
    raise { ConferenceCreated(name, slug) }
  }

  def addSeats(seatType: String, quota: Int): Unit = state.seats.get(seatType) match {
    case None => raise { SeatsAdded(id, seatType, quota) }
    case Some(_) => logger.warn(s"Discard seats addition on existing seats (type: $seatType, conference: $id)")
  }

  def makeSeatsReservation(orderId: String, seatsRequest: (String, Int)){
    if (state.published) {
      val (seatType, quantity) = seatsRequest

      state.seats.get(seatType) match {
        case Some(remainingSeats) if quantity <= remainingSeats =>
          raise { SeatsReserved(id, orderId, seats = seatType -> quantity) }

        case Some(remainingSeats) =>
          logger.warn(s"Reject seats reservation due to insufficient seat quota (id: $id, $quantity > $remainingSeats)")
          raise { SeatsReservationRejected(id, orderId, seatsRequest) }

        case None =>
          logger.warn(s"Reject seats reservation due to a missing seat type (id: $id)")
          raise { SeatsReservationRejected(id, orderId, seatsRequest) }
      }
    } else {
      logger.warn(s"Reject seats reservation for a conference that is not published yet (id: $id)")
      raise { SeatsReservationRejected(id, orderId, seatsRequest) }
    }
  }

  def publish(){
    if (!state.published) {
      raise { ConferencePublished(id) }
    }
  }

  def update(name: String): Unit = raise { ConferenceUpdated(id, name) }

  case class ConferenceDecisionProjection(
    conferenceName: String = "",
    conferenceSlug: String = "",
    published: Boolean = false,
    seats: Map[String, Int] = empty
  ) extends DecisionProjection {

    def apply(event: ConferenceEvent) = event match {
      case ConferenceCreated(name, slug) => copy(name, slug)
      case ConferencePublished(_) => copy(published = true)
      case ConferenceUpdated(_, name) => copy(name)
      case SeatsAdded(_, seatType, quota) => copy(seats = seats + (seatType -> quota))
      case SeatsReserved(_, _, (seatType, quantity)) =>
        val remainingSeats = seats(seatType) - quantity
        copy(seats = seats + (seatType -> remainingSeats))
      case _: SeatsReservationRejected => this
    }
  }
}

object Conference {

  implicit val rehydrateAggregate: (String, Seq[ConferenceEvent]) => Conference = apply

  def apply(name: String, slug: String) = new Conference(name, slug)

  def apply(id: String, history: Seq[ConferenceEvent]) = {
    if (history.isEmpty) {
      throw new IllegalArgumentException("Either create a new conference from a slug or provide an history")
    }

    new Conference(id, history)
  }
}
