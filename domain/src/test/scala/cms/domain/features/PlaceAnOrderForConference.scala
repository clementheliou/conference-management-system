package cms.domain.features

import cms.domain.features.stages.{GivenAConference, ThenSeatsAreReserved, WhenRegistrantPlaceOrder}
import com.tngtech.jgiven.junit.ScenarioTest
import org.junit.Test
import org.scalatest.junit.JUnitSuiteLike

class PlaceAnOrderForConference
  extends ScenarioTest[GivenAConference, WhenRegistrantPlaceOrder, ThenSeatsAreReserved] with JUnitSuiteLike {

  @Test
  def successfully_place_an_order_for_a_single_conference_seat_type_with_enough_quota(){
    given a_conference_with_a_quota_of_$_$_seats(10, "Workshop")

    when a_registrant_place_an_order_for_$_$_seats(8, "Workshop")

    `then` $_$_seats_are_reserved(8, "Workshop")
  }

  @Test
  def place_an_order_for_a_single_conference_seat_type_with_insufficient_quota(){
    given a_conference_with_a_quota_of_$_$_seats(10, "Workshop")

    when a_registrant_place_an_order_for_$_$_seats(11, "Workshop")

    `then` the_$_$_seats_are_not_reserved(11, "Workshop")
  }
}
