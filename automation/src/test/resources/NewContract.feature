Feature: New Contract
  This is a test feature for the 5250 terminal test automation using business objects

  Scenario Outline: 1. New Contract Proposal

    Given I am connected to NRO "<environment>" with "<user>" and "<password>"
    And I navigate to New Contract Proposal
    And I create a new Contract of type "<type>"

    Examples:
      | user    | password   | environment | type  |
      | GIUROAL | Bucuresti2 | 72          | 1R1   |