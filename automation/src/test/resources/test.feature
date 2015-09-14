# Created by Alexandru Giurovici at 14.09.2015
Feature: Test Feature
  This is a test feature for the 5250 terminal test automation

  Scenario: Login
    Given I am connected to NRO
    And I login
    Then I should be on the main page