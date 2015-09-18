Feature: Business Objects Test Feature
  This is a test feature for the 5250 terminal test automation using business objects

  Scenario Outline: 1. Login

    Given I am connected to NRO "<environment>" with "<user>" and "<password>"
    And I navigate to Clients menu

    Examples:
      | user    | password   | environment |
      | GIUROAL | Bucuresti2 | 72          |