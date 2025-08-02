#language: en

@reg @addBook @post
Feature: Add Books API

  Scenario: Add new book
    Given I set params
      | pagecount | 99  |
    When I send POST request to BOOKS
    Then I receive response code 200
    Then response matches schema BOOK_DETAILS
    And response contains: pageCount with value 99

  Scenario: Add new book - empty payload
    When I send POST request to: BOOKS with bodyfile EMPTY
    Then I receive response code 400

  Scenario: Add new book - lacking title parameter in body
    Given I set params
      | id | 1 |
    When I send POST request to: BOOKS with bodyfile BOOKS_LACKING_TITLE
    Then I receive response code 400
    Then response matches schema ERROR

  Scenario: Add new book - wrong pagecount
    Given I set params
      | pagecount | TEST |
    When I send POST request to BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.Int32

  Scenario: Add new book - negative pagecount
    Given I set params
      | pagecount | -9 |
    When I send POST request to BOOKS
    Then I receive response code 400
    Then response matches schema ERROR

  Scenario: Add new book - wrong method
    When I send PUT request to BOOKS with bodyfile BOOKS
    Then I receive response code 405

  Scenario: Add new book - existing id
    Given I set params
      | id | 10  |
    When I send POST request to BOOKS
    Then I receive response code 409
    Then response matches schema ERROR

  Scenario: Add new book - incorrect id format
    Given I set params
      | id | abc  |
    When I send POST request to BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.Int32

  Scenario: Add new book - incorrect timestamp
    Given I set params
      | publishdate | NOW  |
    When I send POST request to BOOKS
    Then I receive response code 400
    Then response matches schema ERROR
    And response contains The JSON value could not be converted to System.DateTime