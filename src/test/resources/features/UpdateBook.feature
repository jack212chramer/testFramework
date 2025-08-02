#language: en

@reg @updateBook @put
Feature: Update Books API

  Scenario: Update book
    Given I set params
      | id | 1 |
      | pagecount | 250 |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 200
    Then response matches schema BOOK_DETAILS
    And response contains: pageCount with value 250

  Scenario: Update book - empty payload
    When I send PUT request to BOOKS with bodyfile EMPTY
    Then I receive response code 405

  Scenario: Add new book - lacking title parameter in body
    Given I set params
      | id | 1 |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS_LACKING_TITLE
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.Int32

  Scenario: Update book - wrong pagecount
    Given I set params
      | id | 1 |
      | pagecount | TEST |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.Int32

  Scenario: Update book - negative pagecount
    Given I set params
      | id | 1 |
      | pagecount | -9 |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 400
    Then response matches schema ERROR

  Scenario: Update book - wrong method
    Given I set params
      | id | 1 |
    When I send POST request to: BOOKS_ID with bodyfile BOOKS
    Then I receive response code 405

  Scenario: Update book - non-existing id
    Given I set params
      | id | -99  |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 409
    Then response matches schema ERROR

  Scenario: Update book - incorrect id format
    Given I set params
      | id | abc  |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.Int32

  Scenario: Update book - incorrect timestamp
    Given I set params
      | id | 1 |
      | publishdate | NOW  |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.DateTime

  Scenario: Update book - weird ascii characters in title
    Given I set params
      | id | 1 |
      | title | 🌈@'; |
    When I send PUT request to BOOKS_ID with bodyfile BOOKS
    Then I receive response code 200
    Then response matches schema BOOK_DETAILS
    And response contains: title with value 🌈@';
