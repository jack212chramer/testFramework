#language: en

@reg @deleteBook @delete
Feature: Delete Books API

  Scenario: Delete book
    Given I set params
      | id | 1 |
    When I send DELETE request to BOOKS_ID
    Then I receive response code 200

  Scenario: Delete book - non-existing id
    Given I set params
      | id | -99  |
    When I send DELETE request to BOOKS_ID
    Then I receive response code 404
    Then response matches schema ERROR

  Scenario: Delete book - incorrect id format
    Given I set params
      | id | abc  |
    When I send DELETE request to BOOKS_ID
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The value 'abc' is not valid.