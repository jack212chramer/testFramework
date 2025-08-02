#language: en

@reg @getBook @get
Feature: Get Books API

  Scenario: Get book list
    When I send GET request to BOOKS
    Then I receive response code 200
    Then response matches schema BOOK_LIST

  Scenario: Get book by ID
    Given I set params
      | id | 1  |
    When I send GET request to BOOKS_ID
    Then I receive response code 200
    Then response matches schema BOOK_DETAILS

  Scenario: Get book by ID in query param instead of path param
    When I send GET request with params to BOOKS
      | id | 1  |
    Then I receive response code 200
    Then response matches schema BOOK_LIST

  Scenario: Get book by non existing ID
    Given I set params
      | id | -1  |
    When I send GET request to BOOKS_ID
    Then I receive response code 404
    Then response matches schema ERROR
    And response contains: title with value Not Found

  Scenario: Get book by ID - sql injection
    Given I set params
      | id | ' OR 1=1;--  |
    And I will not encode path parameters
    When I send GET request to BOOKS_ID
    Then I receive response code 400
    Then response matches schema ERROR