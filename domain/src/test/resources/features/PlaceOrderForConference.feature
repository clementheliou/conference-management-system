Feature: Place an order for a conference

  Scenario: Successfully place an order for a single conference seat type with enough quota

    Given a published conference with a quota of 10 Workshop seats
    When a registrant place an order for 8 Workshop seats
    Then the 8 Workshop seats are successfully reserved