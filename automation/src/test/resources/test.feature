# Created by Alexandru Giurovici at 14.09.2015
Feature: Test Feature
  This is a test feature for the 5250 terminal test automation

  Scenario Outline: 1. Login

    Given I am connected to NRO
    And I login with "<user>" and "<password>"
    And I should be on the main page of "<environment>"
    Then I navigate to contract creation
    And I add personal client
    And I create a new person


    Examples:
      | user    | password   | environment |
      | GIUROAL | Bucuresti1 | 72          |
