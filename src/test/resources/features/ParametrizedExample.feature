#language: en

@reg @addBook @post
Feature: Add Books API - Parametrized test example

  Scenario Outline: Add new book - parametrized test example
    Given I set params
      | id | <id>  |
      | pagecount | <pagecount> |
      | publishdate | <publishedDate> |
    When I send POST request to BOOKS
    Then I receive response code <expectedCode>
    Then response matches schema <expectedResponseSchema>

    Examples:
      | id  | pagecount | publishedDate                     | expectedCode | expectedResponseSchema | description |
      | -1  | 100       | 2025-08-01T14:26:57.0497392+00:00 | 200          | BOOK_DETAILS           | Happy path |
      | -1  | ABC       | 2025-08-01T14:26:57.0497392+00:00 | 400          | ERROR                  | Invalid page count |
      | -1  | 100       | NOW                               | 400          | ERROR                  | Invalid date format |
      |  1  | 100       | 2025-08-01T14:26:57.0497392+00:00 | 400          | ERROR                  | Book already exists |


