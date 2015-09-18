Feature: New Contracts
  This is a proof of concept feature for the 5250 terminal using business objects

  Background:

    Given I am connected to NRO "72" with "GIUROAL" and "Bucuresti2"
    And I navigate to New Contract Proposal

  Scenario Outline: 1. New Contract Proposal

    Given I create a new Contract of type "<type>"
    When I set the contract owner to "<owner>"
    And I set the date to "<date>"
    And I set the billing frequency to "<freq>"
    And I set the method of payment to "<method>"
    And I set the serial number to "<serial>"
    And I set the agent to "<agent>"
    Then I go back

    Examples:
      | type  | owner   | date       | freq | method | serial | agent  |
      | 1R1   | Adrian  | 01/01/2015 |      | C      | 1337   | Vasile |